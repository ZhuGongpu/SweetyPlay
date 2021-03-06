package app.view.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import app.DemoApplication;
import app.view.chat.adapter.ChatAllHistoryAdapter;
import app.view.login.R;
import app.view.main.MainActivity;
import com.easemob.chat.*;
import easemob.db.InviteMessageDao;
import easemob.domain.User;

import java.util.*;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class ChatAllHistoryFragment extends Fragment {

    public RelativeLayout errorItem;
    public TextView errorText;
    private InputMethodManager inputMethodManager;
    private ListView listView;
    private Map<String, User> contactList;
    private ChatAllHistoryAdapter adapter;
    private EditText query;
    private ImageButton clearSearch;
    private boolean hidden;
    private List<EMGroup> groups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        // contact list
        contactList = DemoApplication.getInstance().getContactList();
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, loadConversationsWithRecentChat());

        groups = EMGroupManager.getInstance().getAllGroups();

        // 设置adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(DemoApplication.getInstance().getUsername()))
                    Toast.makeText(getActivity(), "不能和自己聊天", Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    EMContact emContact = null;
                    groups = EMGroupManager.getInstance().getAllGroups();
                    for (EMGroup group : groups) {
                        if (group.getGroupId().equals(username)) {
                            emContact = group;
                            break;
                        }
                    }
                    if (emContact != null && emContact instanceof EMGroup) {
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", ((EMGroup) emContact).getGroupId());
                    } else {
                        // it is single chat
                        intent.putExtra("userId", username);
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        // 搜索框
        query = (EditText) getView().findViewById(R.id.query);
        // 搜索框中清除button
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            EMConversation tobeDeleteCons = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            // 删除此会话
            EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
            InviteMessageDao inviteMessageDao = new InviteMessageDao(getActivity());
            inviteMessageDao.deleteMessage(tobeDeleteCons.getUserName());
            adapter.remove(tobeDeleteCons);
            adapter.notifyDataSetChanged();

            // 更新消息未读数
            ((MainActivity) getActivity()).updateUnreadLabel();

            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        adapter = new ChatAllHistoryAdapter(getActivity(), R.layout.row_chat_history, loadConversationsWithRecentChat());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     *
     * @return
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<EMConversation> conversationList = new ArrayList<EMConversation>();
        //过滤掉messages seize为0的conversation
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() != 0)
                conversationList.add(conversation);
        }
        // 排序
        sortConversationByLastChatTime(conversationList);
        return conversationList;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(final EMConversation con1, final EMConversation con2) {

                EMMessage con2LastMessage = con2.getLastMessage();
                EMMessage con1LastMessage = con1.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

}
