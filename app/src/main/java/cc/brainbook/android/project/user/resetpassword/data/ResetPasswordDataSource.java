package cc.brainbook.android.project.user.resetpassword.data;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cc.brainbook.android.project.user.resetpassword.data.model.ResetPasswordUser;
import cc.brainbook.android.project.user.resetpassword.exception.ResetPasswordException;
import cc.brainbook.android.project.user.resetpassword.interfaces.ResetPasswordCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cc.brainbook.android.project.user.config.Config.CONNECT_TIMEOUT;
import static cc.brainbook.android.project.user.config.Config.RESET_PASSWORD_CHECK_SEND_MODE_URL;
import static cc.brainbook.android.project.user.config.Config.RESET_PASSWORD_FIND_USER_URL;
import static cc.brainbook.android.project.user.config.Config.RESET_PASSWORD_RESET_PASSWORD_URL;
import static cc.brainbook.android.project.user.config.Config.RESET_PASSWORD_SEND_VERIFICATION_CODE_URL;
import static cc.brainbook.android.project.user.config.Config.RESET_PASSWORD_VERIFY_CODE_URL;

public class ResetPasswordDataSource {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESET_PASSWORD_USER = "resetPasswordUser";
    private static final String KEY_USER_ID = "openId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_SEND_MODE = "sendMode";
    private static final String KEY_SESSION_ID = "sessionId";
    private static final String KEY_VERIFICATION_CODE = "verificationCode";

    public void findUser(String username, final ResetPasswordCallback resetPasswordCallback) {
        ///https://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
        final JSONObject jsonObject = new JSONObject();
        try {
            //Populate the sendVerificationCode parameters
            jsonObject.put(KEY_USERNAME, username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 构建post的RequestBody
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        // 创建一个Request
        final Request request = new Request.Builder()
                .url(RESET_PASSWORD_FIND_USER_URL)
                .post(requestBody)
                .build();

        // 创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // 请求加入调度
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ///[返回结果及错误处理]错误处理
                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_IO_EXCEPTION, e.getCause()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = null;
                            if (response.body() != null) {
                                jsonObject = new JSONObject(response.body().string());
                            }

                            if (jsonObject != null) {
                                ///[返回结果及错误处理]
                                switch (jsonObject.getInt(KEY_STATUS)) {
                                    case -1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_PARAMETERS, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 0:
                                        final ResetPasswordUser resetPasswordUser =  new Gson().fromJson(jsonObject.getString(KEY_RESET_PASSWORD_USER), ResetPasswordUser.class);
                                        resetPasswordCallback.onSuccess(resetPasswordUser);
                                        break;
                                    case 1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_USERNAME, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 2:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_CANNOT_RESET_PASSWORD, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    default:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_UNKNOWN));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void checkSendMode(String userId, String email, String mobile, final ResetPasswordCallback resetPasswordCallback) {
        ///https://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
        final JSONObject jsonObject = new JSONObject();
        try {
            //Populate the sendVerificationCode parameters
            jsonObject.put(KEY_USER_ID, userId);
            jsonObject.put(KEY_EMAIL, email);
            jsonObject.put(KEY_MOBILE, mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 构建post的RequestBody
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        // 创建一个Request
        final Request request = new Request.Builder()
                .url(RESET_PASSWORD_CHECK_SEND_MODE_URL)
                .post(requestBody)
                .build();

        // 创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // 请求加入调度
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ///[返回结果及错误处理]错误处理
                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_IO_EXCEPTION, e.getCause()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = null;
                            if (response.body() != null) {
                                jsonObject = new JSONObject(response.body().string());
                            }

                            if (jsonObject != null) {
                                ///[返回结果及错误处理]
                                switch (jsonObject.getInt(KEY_STATUS)) {
                                    case -1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_PARAMETERS, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 0:
                                        final int sendMode = jsonObject.getInt(KEY_SEND_MODE);
                                        resetPasswordCallback.onSuccess(sendMode);
                                        break;
                                    case 1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_USER_ID, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 2:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_CANNOT_RESET_PASSWORD, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 3:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_NO_MATCH_EMAIL, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 4:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_NO_MATCH_MOBILE, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 5:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_NO_MATCH_EMAIL_OR_MOBILE, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    default:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_UNKNOWN));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void sendVerificationCode(String userId, int sendMode, final ResetPasswordCallback resetPasswordCallback) {
        ///https://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
        final JSONObject jsonObject = new JSONObject();
        try {
            //Populate the sendVerificationCode parameters
            jsonObject.put(KEY_USER_ID, userId);
            jsonObject.put(KEY_SEND_MODE, sendMode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 构建post的RequestBody
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        // 创建一个Request
        final Request request = new Request.Builder()
                .url(RESET_PASSWORD_SEND_VERIFICATION_CODE_URL)
                .post(requestBody)
                .build();

        // 创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // 请求加入调度
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ///[返回结果及错误处理]错误处理
                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_IO_EXCEPTION, e.getCause()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = null;
                            if (response.body() != null) {
                                jsonObject = new JSONObject(response.body().string());
                            }

                            if (jsonObject != null) {
                                ///[返回结果及错误处理]
                                switch (jsonObject.getInt(KEY_STATUS)) {
                                    case -1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_PARAMETERS, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 0:
                                        resetPasswordCallback.onSuccess(jsonObject.getString(KEY_SESSION_ID));
                                        break;
                                    case 1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_USER_ID, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 2:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_CANNOT_RESET_PASSWORD, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 3:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_FAILED_TO_SEND_EMAIL, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 4:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_FAILED_TO_SEND_MOBILE, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 5:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_FAILED_TO_SEND_EMAIL_AND_MOBILE, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    default:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_UNKNOWN));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void verifyCode(String sessionId, String verificationCode, final ResetPasswordCallback resetPasswordCallback) {
        ///https://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
        final JSONObject jsonObject = new JSONObject();
        try {
            //Populate the sendVerificationCode parameters
            jsonObject.put(KEY_SESSION_ID, sessionId);
            jsonObject.put(KEY_VERIFICATION_CODE, verificationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 构建post的RequestBody
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        // 创建一个Request
        final Request request = new Request.Builder()
                .url(RESET_PASSWORD_VERIFY_CODE_URL)
                .post(requestBody)
                .build();

        // 创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // 请求加入调度
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ///[返回结果及错误处理]错误处理
                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_IO_EXCEPTION, e.getCause()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = null;
                            if (response.body() != null) {
                                jsonObject = new JSONObject(response.body().string());
                            }

                            if (jsonObject != null) {
                                ///[返回结果及错误处理]
                                switch (jsonObject.getInt(KEY_STATUS)) {
                                    case -1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_PARAMETERS, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 0:
                                        resetPasswordCallback.onSuccess(null);
                                        break;
                                    case 1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_VERIFICATION_CODE, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    default:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_UNKNOWN));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void resetPassword(String userId, String password, final ResetPasswordCallback resetPasswordCallback) {
        ///https://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
        final JSONObject jsonObject = new JSONObject();
        try {
            //Populate the sendVerificationCode parameters
            jsonObject.put(KEY_USER_ID, userId);
            jsonObject.put(KEY_PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 构建post的RequestBody
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        // 创建一个Request
        final Request request = new Request.Builder()
                .url(RESET_PASSWORD_RESET_PASSWORD_URL)
                .post(requestBody)
                .build();

        // 创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // 请求加入调度
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ///[返回结果及错误处理]错误处理
                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_IO_EXCEPTION, e.getCause()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = null;
                            if (response.body() != null) {
                                jsonObject = new JSONObject(response.body().string());
                            }

                            if (jsonObject != null) {
                                ///[返回结果及错误处理]
                                switch (jsonObject.getInt(KEY_STATUS)) {
                                    case -1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_INVALID_PARAMETERS, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    case 0:
                                        resetPasswordCallback.onSuccess(null);
                                        break;
                                    case 1:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_CANNOT_RESET_PASSWORD, jsonObject.getString(KEY_MESSAGE)));
                                        break;
                                    default:
                                        resetPasswordCallback.onError(new ResetPasswordException(ResetPasswordException.EXCEPTION_UNKNOWN));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
