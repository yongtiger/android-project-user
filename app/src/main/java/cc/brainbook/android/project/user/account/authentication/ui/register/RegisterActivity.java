package cc.brainbook.android.project.user.account.authentication.ui.register;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cc.brainbook.android.project.user.R;
import cc.brainbook.android.project.user.oauth.AccessToken;
import cc.brainbook.android.project.user.oauth.config.Config;
import cc.brainbook.android.project.user.result.Result;
import cc.brainbook.android.project.user.account.authentication.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private RegisterViewModel registerViewModel;

    private EditText etUsername;
    private ImageView ivClearUsername;
    private EditText etPassword;
    private ImageView ivClearPassword;
    private ImageView ivPasswordVisibility;
    private EditText etRepeatPassword;
    private ImageView ivClearRepeatPassword;
    private ImageView ivRepeatPasswordVisibility;

    private Button btnRegister;
    private Button btnLogin;

    private ProgressBar pbLoading;

    ///[oAuth#NetworkAccessTokenMap]
    private HashMap<Config.Network, AccessToken> networkAccessTokenMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ///[oAuth#NetworkAccessTokenMap]
        if (getIntent() != null) {
            networkAccessTokenMap = (HashMap<Config.Network, AccessToken>) getIntent().getSerializableExtra("networkAccessTokenMap");
        }

        initView();
        initListener();

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.
        // Note: A ViewModel must never reference a view, Lifecycle, or any class that may hold a reference to the activity context.
        ///https://developer.android.com/topic/libraries/architecture/viewmodel
        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory(false, false))  ///[EditText显示/隐藏Password]初始化
                .get(RegisterViewModel.class);

        registerViewModel.getRegisterFormStateLiveData().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                btnRegister.setEnabled(registerFormState.isDataValid());

                ///[EditText错误提示]
                if (registerFormState.getUsernameError() == null) {
                    etUsername.setError(null);
                } else {
                    etUsername.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() == null) {
                    etPassword.setError(null);
                } else {
                    etPassword.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getRepeatPasswordError() == null) {
                    etRepeatPassword.setError(null);
                } else {
                    etRepeatPassword.setError(getString(registerFormState.getRepeatPasswordError()));
                }
            }
        });

        registerViewModel.getResultLiveData().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(@Nullable Result result) {
                if (result == null) {
                    return;
                }
                pbLoading.setVisibility(View.GONE);
                if (result.getError() != null) {
                    ///[Request focus#根据返回错误来请求表单焦点]
                    switch (result.getError()) {
                        case R.string.error_network_error:
                            break;
                        case R.string.error_invalid_parameters:
                            break;
                        case R.string.register_error_user_exists:
                            etUsername.requestFocus();
                            break;
                        default:    ///R.string.error_unknown
                    }
                    ///Display register failed
                    showRegisterFailed(result.getError());
                } else {
                    ///[oAuth#NetworkAccessTokenMap]
                    if (networkAccessTokenMap.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.result_success_register), Toast.LENGTH_LONG).show();
                    } else {
                        ///[oAuth#NetworkAccessTokenMap]返回账户注册成功、绑定的network列表
                        final StringBuilder content = new StringBuilder();
                        for (Map.Entry<Config.Network, AccessToken> networkAccessTokenEntry : networkAccessTokenMap.entrySet()) {
                            final Config.Network network = (Config.Network) ((HashMap.Entry) networkAccessTokenEntry).getKey();
//                            AccessToken accessToken = (AccessToken) entry.getValue();
                            content.append(", ").append(network);
                        }
                        Toast.makeText(getApplicationContext(), getString(R.string.result_success_oauth_bind_register)
                                + content, Toast.LENGTH_LONG).show();
                    }

                    setResult(Activity.RESULT_OK);

                    //Complete and destroy register activity once successful
                    finish();
                }
            }
        });

        registerViewModel.getPasswordVisibilityLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
                if(aBoolean){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivPasswordVisibility.setImageResource(R.drawable.ic_visibility);
                }else{
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivPasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
                }
                final String pwd = etPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    etPassword.setSelection(pwd.length());
            }
        });

        registerViewModel.getRepeatPasswordVisibilityLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
                if(aBoolean){
                    etRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivRepeatPasswordVisibility.setImageResource(R.drawable.ic_visibility);
                }else{
                    etRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivRepeatPasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
                }
                final String pwd = etRepeatPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    etRepeatPassword.setSelection(pwd.length());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear_username:
                ///[EditText清除输入框]
                etUsername.setText("");
                break;
            case R.id.iv_clear_password:
                ///[EditText清除输入框]
                etPassword.setText("");
                break;
            case R.id.iv_password_visibility:
                ///[EditText显示/隐藏Password]
                ///注意：因为初始化了，所以不会产生NullPointerException
                registerViewModel.setPasswordVisibilityLiveData(!registerViewModel.getPasswordVisibilityLiveData().getValue());
                break;
            case R.id.iv_clear_repeat_password:
                ///[EditText清除输入框]
                etRepeatPassword.setText("");
                break;
            case R.id.iv_repeat_password_visibility:
                ///[EditText显示/隐藏Password]
                ///注意：因为初始化了，所以不会产生NullPointerException
                registerViewModel.setRepeatPasswordVisibilityLiveData(!registerViewModel.getRepeatPasswordVisibilityLiveData().getValue());
                break;
            case R.id.btn_register:
                pbLoading.setVisibility(View.VISIBLE);
                actionRegister();
                break;
            case R.id.btn_login:
                final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    ///避免重复打开LoginActivity（也可以设置LoginActivity为singleTop或singleTask）
                startActivity(intent);
                ///Destroy register activity
                finish();
                break;
        }
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        ivClearUsername = findViewById(R.id.iv_clear_username);
        etPassword = findViewById(R.id.et_password);
        ivClearPassword = findViewById(R.id.iv_clear_password);
        ivPasswordVisibility = findViewById(R.id.iv_password_visibility);
        etRepeatPassword = findViewById(R.id.et_repeat_password);
        ivClearRepeatPassword = findViewById(R.id.iv_clear_repeat_password);
        ivRepeatPasswordVisibility = findViewById(R.id.iv_repeat_password_visibility);

        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        pbLoading = findViewById(R.id.pb_loading);
    }

    private void initListener() {
        ivClearUsername.setOnClickListener(this);
        ivClearPassword.setOnClickListener(this);
        ivPasswordVisibility.setOnClickListener(this);
        ivClearRepeatPassword.setOnClickListener(this);
        ivRepeatPasswordVisibility.setOnClickListener(this);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ///[EditText错误提示]
                registerViewModel.registerDataChanged(etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etRepeatPassword.getText().toString());

                ///[EditText清除输入框]
                if (!TextUtils.isEmpty(s) && ivClearUsername.getVisibility() == View.GONE) {
                    ivClearUsername.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    ivClearUsername.setVisibility(View.GONE);
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ///[EditText错误提示]
                registerViewModel.registerDataChanged(etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etRepeatPassword.getText().toString());

                ///[EditText清除输入框]
                if (!TextUtils.isEmpty(s) && ivClearPassword.getVisibility() == View.GONE) {
                    ivClearPassword.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    ivClearPassword.setVisibility(View.GONE);
                }
            }
        });
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    actionRegister();
                }
                return false;
            }
        });
        etRepeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ///[EditText错误提示]
                registerViewModel.registerDataChanged(etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etRepeatPassword.getText().toString());

                ///[EditText清除输入框]
                if (!TextUtils.isEmpty(s) && ivClearRepeatPassword.getVisibility() == View.GONE) {
                    ivClearRepeatPassword.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    ivClearRepeatPassword.setVisibility(View.GONE);
                }
            }
        });

        etRepeatPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    actionRegister();
                }
                return false;
            }
        });
    }

    private void actionRegister() {
        ///[FIX#IME_ACTION_DONE没检验form表单状态]
        if (registerViewModel.getRegisterFormStateLiveData().getValue() != null
                && registerViewModel.getRegisterFormStateLiveData().getValue().isDataValid()) {
            registerViewModel.register(etUsername.getText().toString(),
                    etPassword.getText().toString(), networkAccessTokenMap);
        }
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}