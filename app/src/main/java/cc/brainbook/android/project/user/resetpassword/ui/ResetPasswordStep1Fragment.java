package cc.brainbook.android.project.user.resetpassword.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

import cc.brainbook.android.project.user.R;
import cc.brainbook.android.project.user.result.Result;

public class ResetPasswordStep1Fragment extends Fragment implements View.OnClickListener {

    private EditText etUsername;
    private ImageView ivClearUsername;

    private Button btnNext;
    private ProgressBar pbLoading;

    private ResetPasswordViewModel resetPasswordViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ResetPasswordStep1Fragment() {}

    /**
     * Create a new instance of fragment.
     */
    public static ResetPasswordStep1Fragment newInstance() {
        return new ResetPasswordStep1Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.
        // Note: A ViewModel must never reference a view, Lifecycle, or any class that may hold a reference to the activity context.
        ///https://developer.android.com/topic/libraries/architecture/viewmodel
        resetPasswordViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), new ResetPasswordViewModelFactory())
                .get(ResetPasswordViewModel.class);

        resetPasswordViewModel.getResetPasswordStep1FormStateLiveData().observe(this, new Observer<ResetPasswordStep1FormState>() {
            @Override
            public void onChanged(@Nullable ResetPasswordStep1FormState resetPasswordStep1FormState) {
                if (resetPasswordStep1FormState == null) {
                    return;
                }
                btnNext.setEnabled(resetPasswordStep1FormState.isDataValid());

                ///[EditText错误提示]
                if (resetPasswordStep1FormState.getUsernameError() == null) {
                    etUsername.setError(null);
                } else {
                    etUsername.setError(getString(resetPasswordStep1FormState.getUsernameError()));
                }
            }
        });

        resetPasswordViewModel.setResultLiveData();
        resetPasswordViewModel.getResultLiveData().observe(this, new Observer<Result>() {
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
                        case R.string.error_unknown:
                            break;
                        case R.string.error_invalid_parameters:
                            break;
                        case R.string.error_invalid_username:
                            etUsername.requestFocus();
                            break;
                        case R.string.result_error_cannot_reset_password:
                            break;
                        default:    ///R.string.error_unknown
                    }

                    ///Display failed message
                    if (getActivity() != null) {
                        ((ResetPasswordActivity)getActivity()).showFailedMessage(result.getError());
                    }
                } else {
                    if (getActivity() != null) {
                        if (result.getSuccess() != null)
                            ((ResetPasswordActivity) getActivity()).updateUI(result.getSuccess());
                        ((ResetPasswordActivity)getActivity()).showResetPasswordStep2Fragment();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_reset_password_step_1, container, false);

        initView(rootView);
        initListener();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear_username:
                ///[EditText清除输入框]
                etUsername.setText("");
                break;
            case R.id.btn_next:
                pbLoading.setVisibility(View.VISIBLE);
                actionNext();
                break;
        }
    }

    private void initView(@NonNull View rootView) {
        etUsername = rootView.findViewById(R.id.et_username);
        ivClearUsername = rootView.findViewById(R.id.iv_clear_username);

        btnNext = rootView.findViewById(R.id.btn_next);
        pbLoading = rootView.findViewById(R.id.pb_loading);
    }

    private void initListener() {
        etUsername.setOnClickListener(this);
        ivClearUsername.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ///[EditText错误提示]
                resetPasswordViewModel.resetPasswordStep1DataChanged(etUsername.getText().toString());

                ///[EditText清除输入框]
                if (!TextUtils.isEmpty(s) && ivClearUsername.getVisibility() == View.GONE) {
                    ivClearUsername.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    ivClearUsername.setVisibility(View.GONE);
                }
            }
        });

        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    actionNext();
                }
                return false;
            }
        });
    }

    private void actionNext() {
        if (resetPasswordViewModel.getResetPasswordStep1FormStateLiveData().getValue() != null
                && resetPasswordViewModel.getResetPasswordStep1FormStateLiveData().getValue().isDataValid()) {
            resetPasswordViewModel.findUser(etUsername.getText().toString());
        }
    }
}
