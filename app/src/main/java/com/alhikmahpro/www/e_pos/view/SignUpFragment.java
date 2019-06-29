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
public class SignUpFragment extends Fragment {

    FragmentActionListener fragmentActionListener;
    @BindView(R.id.editShopName)
    EditText editShopName;
    @BindView(R.id.buttonLogin)
    Button buttonLogin;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    Unbinder unbinder;
    @BindView(R.id.editEmail)
    EditText editEmail;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.editPasswordConfirm)
    EditText editPasswordConfirm;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        unbinder = ButterKnife.bind(this, view);
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

    @OnClick(R.id.buttonLogin)
    public void onViewClicked() {

        String shopName = editShopName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        if (validate(shopName,email,password)) {


            Bundle bundle = new Bundle();
            bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT, 2);
            bundle.putString("NAME", shopName);
            bundle.putString("EMAIL", email);
            //bundle.putString("DEVICE", deviceId);
            bundle.putString("PASSWORD", password);

            // bundle.putString("POWER","Admin");
            fragmentActionListener.onActionPerformed(bundle);

        }


    }


    private boolean validate(String shop,String email,String pass) {

        if (TextUtils.isEmpty(shop)) {
            editShopName.setError("Enter Shop Name");
            return false;
        }
         else if (TextUtils.isEmpty(email)||!email.matches(emailPattern)) {
            editEmail.setError("Enter Valid Email Address");
            return false;

        } else if (TextUtils.isEmpty(pass)) {
            editPassword.setError("Enter Password");
            return false;
        }
        return true;

    }

}

