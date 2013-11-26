package com.find.guide.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.find.guide.activity.MainActivity;
import com.find.guide.config.AppConfig;
import com.find.guide.setting.SettingManager;
import com.find.guide.user.Tourist;
import com.plugin.internet.core.JsonUtils;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {

    public static final String TAG = PushMessageReceiver.class.getSimpleName();

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            // 获取消息内容
            String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            AppConfig.LOGD("Receive message from server: " + message);

            if (!TextUtils.isEmpty(message) && SettingManager.getInstance().getUserId() > 0) {
                Message msg = JsonUtils.parse(message, Message.class);
                if (msg != null && msg.toId == SettingManager.getInstance().getUserId()) {
                    if (SettingManager.getInstance().getUserType() == Tourist.USER_TYPE_TOURGUIDE) {
                        if (msg.type == Message.MSG_TYPE_INVITE || msg.type == Message.MSG_TYPE_BROADCAST
                                || msg.type == Message.MSG_TYPE_EVALUATED) {
                            SettingManager.getInstance().setGuideMode(0);
                        } else if (msg.type == Message.MSG_TYPE_ACCEPTED || msg.type == Message.MSG_TYPE_REFUSED) {
                            SettingManager.getInstance().setGuideMode(1);
                        }
                    }
                    NotificationHelper.getInstance(context).notifyMessage(msg);
                }
            }
        } else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
            // 获取方法
            String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
            int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
            String content = "";
            if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
                content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
            }
            if (PushConstants.METHOD_BIND.equals(method)) {
                if (errorCode == 0) {
                    try {
                        JSONObject jsonContent = new JSONObject(content);
                        JSONObject params = jsonContent.getJSONObject("response_params");
                        String appid = params.getString("appid");
                        String channelid = params.getString("channel_id");
                        String userid = params.getString("user_id");

                        AppConfig.LOGD("bind success " + "appid=" + appid + ",channelid=" + channelid + ", userid="
                                + userid);

                        if (SettingManager.getInstance().getUserId() > 0 && !TextUtils.isEmpty(channelid)
                                && !TextUtils.isEmpty(userid)) {
                            new PushHelper(context).bindDevice(channelid, userid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // TODO
                    if (errorCode == 30607) {
                        AppConfig.LOGD("Bind Fail update channel token-----!");
                    }
                }
            }
            // 可选。通知用户点击事件处理
        } else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
            AppConfig.LOGD("intent=" + intent.toUri(0));
            // 自定义内容的json串
            AppConfig.LOGD("EXTRA_EXTRA = " + intent.getStringExtra(PushConstants.EXTRA_EXTRA));

            Intent aIntent = new Intent();
            aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            aIntent.setAction(MainActivity.ACTION_PUSH_CLICK);
            aIntent.setClass(context, MainActivity.class);
            context.startActivity(aIntent);
        }
    }

}
