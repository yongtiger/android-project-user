package cc.brainbook.android.project.login.useraccount.modify;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.SuperTextView;

import cc.brainbook.android.project.login.R;
import cc.brainbook.android.project.login.useraccount.data.UserRepository;
import cc.brainbook.android.project.login.useraccount.data.model.LoggedInUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModifyFragment extends Fragment {

    private SuperTextView stvUsername;
    private SuperTextView stvPassword;
    private SuperTextView stvEmail;
    private SuperTextView stvMobile;
    private SuperTextView stvOauth;

    public ModifyFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of fragment.
     */
    public static ModifyFragment newInstance() {
        return new ModifyFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user_account, container, false);

        initView(rootView);
        initListener();

        return rootView;
    }

    private void initView(@NonNull View rootView) {
        stvUsername = rootView.findViewById(R.id.stv_username);
        stvPassword = rootView.findViewById(R.id.stv_password);
        stvEmail = rootView.findViewById(R.id.stv_email);
        stvMobile = rootView.findViewById(R.id.stv_mobile);
        stvOauth = rootView.findViewById(R.id.stv_oauth);

        final UserRepository userRepository = UserRepository.getInstance();
        final LoggedInUser loggedInUser = userRepository.getLoggedInUser();
        if (loggedInUser != null) {
            stvUsername.setRightString(loggedInUser.getUsername());
            stvEmail.setRightString(loggedInUser.getEmail());
            stvMobile.setRightString(loggedInUser.getMobile());
        }

    }

    private void initListener() {
        stvUsername.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (getActivity() != null) {
                    ((ModifyActivity)getActivity()).showModifyUsernameFragment();
                }
            }
        });
        stvPassword.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (getActivity() != null) {
                    ((ModifyActivity)getActivity()).showModifyPasswordFragment();
                }
            }
        });
        stvEmail.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (getActivity() != null) {
                    ((ModifyActivity)getActivity()).showModifyEmailFragment();
                }
            }
        });
        stvMobile.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (getActivity() != null) {
                    ((ModifyActivity)getActivity()).showModifyMobileFragment();
                }
            }
        });
        stvOauth.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (getActivity() != null) {
                    ((ModifyActivity)getActivity()).showModifyOauthFragment();
                }
            }
        });
    }

}
