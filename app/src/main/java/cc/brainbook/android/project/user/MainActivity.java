package cc.brainbook.android.project.user;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import cc.brainbook.android.project.user.oauth.EasyLogin;
import cc.brainbook.android.project.user.account.modify.ModifyActivity;
import cc.brainbook.android.project.user.account.data.UserRepository;
import cc.brainbook.android.project.user.account.authentication.exception.LogoutException;
import cc.brainbook.android.project.user.account.authentication.interfaces.LogoutCallback;
import cc.brainbook.android.project.user.account.authentication.ui.login.LoginActivity;

import static cc.brainbook.android.project.user.account.authentication.exception.LogoutException.EXCEPTION_INVALID_PARAMETERS;
import static cc.brainbook.android.project.user.account.authentication.exception.LogoutException.EXCEPTION_IO_EXCEPTION;
import static cc.brainbook.android.project.user.account.authentication.exception.LogoutException.EXCEPTION_TOKEN_IS_INVALID_OR_EXPIRED;
import static cc.brainbook.android.project.user.account.authentication.exception.LogoutException.EXCEPTION_UNKNOWN;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOGIN = 1;

    private LinearLayout llLoggedOut;
    private LinearLayout llLoggedIn;
    private SuperTextView stvUserAccount;
    private SuperTextView stvUserProfile;

    private Button btnLogin;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llLoggedOut = findViewById(R.id.ll_logged_out);
        llLoggedIn = findViewById(R.id.ll_logged_in);
        stvUserAccount = findViewById(R.id.stv_user_account);
        stvUserProfile = findViewById(R.id.stv_user_profile);

        btnLogin = findViewById(R.id.btn_login);
        btnLogout = findViewById(R.id.btn_logout);

        stvUserAccount.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                ///start ModifyActivity
                startActivity(new Intent(MainActivity.this, ModifyActivity.class));
            }
        });
        //////???????[UserProfile]
        stvUserProfile.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                ///start UserAccountProfile
//                startActivity(new Intent(MainActivity.this, UserAccountProfile.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), REQUEST_CODE_LOGIN);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///[oAuth]
                final EasyLogin easyLogin = EasyLogin.getInstance();
                easyLogin.logoutAllNetworks();

                final UserRepository userRepository = UserRepository.getInstance();
                userRepository.logout(new LogoutCallback() {
                    @Override
                    public void onSuccess() {
                        ///[返回结果及错误处理]返回结果
                        ///注意：要先获得user！否则会被随后执行的UserRepository#setLoggedInUser(null)清空
                        final String username = userRepository.getLoggedInUser().getUsername();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Logout! " + username, Toast.LENGTH_SHORT).show();

                                ///Update UI
                                updateUI();
                            }
                        });
                    }

                    @Override
                    public void onError(LogoutException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(getErrorIntegerRes(e)), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ///Update UI
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                if (UserRepository.getInstance().isLoggedIn()) {
                    Toast.makeText(getApplicationContext(), "Login! "
                            + UserRepository.getInstance().getLoggedInUser().getUsername(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateUI() {
        if (UserRepository.getInstance().isLoggedIn()) {
            llLoggedIn.setVisibility(View.VISIBLE);
            llLoggedOut.setVisibility(View.GONE);
        } else {
            llLoggedIn.setVisibility(View.GONE);
            llLoggedOut.setVisibility(View.VISIBLE);
        }

        ///Glide下载图片（使用已经缓存的图片）给imageView
        ///https://muyangmin.github.io/glide-docs-cn/doc/getting-started.html
        final RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop()) ///裁剪圆形
                .placeholder(R.drawable.avatar_default); ///   .placeholder(new ColorDrawable(Color.BLACK))   // 或者可以直接使用ColorDrawable
        Glide.with(getApplicationContext())
                .load(UserRepository.getInstance().getLoggedInUser() == null ?
                        null : UserRepository.getInstance().getLoggedInUser().getAvatar())
                .apply(options)
                .into((ImageView) findViewById(R.id.iv_avatar));
    }

    private @StringRes int getErrorIntegerRes(LogoutException e) {
        @StringRes final int error;
        switch (e.getCode()) {
            case EXCEPTION_TOKEN_IS_INVALID_OR_EXPIRED:
                error = R.string.error_token_is_invalid_or_expired;
                break;
            case EXCEPTION_IO_EXCEPTION:
                error = R.string.error_network_error;
                break;
            case EXCEPTION_UNKNOWN:
                error = R.string.error_unknown;
                break;
            case EXCEPTION_INVALID_PARAMETERS:
                error = R.string.error_invalid_parameters;
                break;
            default:
                error = R.string.error_unknown;
        }
        return error;
    }
}
