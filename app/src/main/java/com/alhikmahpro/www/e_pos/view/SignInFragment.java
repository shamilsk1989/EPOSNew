package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    FragmentActionListener fragmentActionListener;
    @BindView(R.id.editEmail)
    EditText editEmail;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.buttonLogin)
    Button buttonLogin;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    Unbinder unbinder;
    Bundle bundle;
    @BindView(R.id.buttonRegister)
    Button buttonRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        unbinder = ButterKnife.bind(this, view);
        bundle = new Bundle();
        //buttonRegister.setEnabled(false);
        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.buttonRegister, R.id.buttonLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonRegister:
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT, 1);
                fragmentActionListener.onActionPerformed(bundle);
                break;
            case R.id.buttonLogin:
                String email = editEmail.getText().toString();
                // String device=editDeviceId.getText().toString();
                String pass = editPassword.getText().toString();
                if (validate(email, pass)) {
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT, 3);
                    bundle.putString("EMAIL", email);
                    //  bundle.putString("DEVICE",device);
                    bundle.putString("PASSWORD", pass);

                }
                fragmentActionListener.onActionPerformed(bundle);
                break;
        }
    }


    private boolean validate(String email, String pass) {


        if (TextUtils.isEmpty(email) || !email.matches(emailPattern)) {
            editEmail.setError("Enter Valid  Email Address");
            return false;

        } else if (TextUtils.isEmpty(pass)) {
            editPassword.setError("Enter Password");
            return false;
        }


        return true;

    }
}
