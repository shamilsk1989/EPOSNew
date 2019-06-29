package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetPinFragment extends Fragment {


    @BindView(R.id.editPin)
    EditText editPin;
    @BindView(R.id.editConfirm)
    EditText editConfirm;
    @BindView(R.id.buttonSave)
    Button buttonSave;
    Unbinder unbinder;
    String title;
    Toolbar toolbar;
    FragmentActionListener fragmentActionListener;

    public SetPinFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_pin, container, false);
        toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        unbinder = ButterKnife.bind(this, view);
        title = getArguments().getString("TITLE");
        toolbar.setTitle(title+"Pin");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @OnClick(R.id.buttonSave)
    public void onViewClicked() {

        String pass=editPin.getText().toString();
        String pass2=editConfirm.getText().toString();
        if(validate(pass,pass2)){
            if(pass.equals(pass2)){

                dbHelper helper=new dbHelper(getContext());
                helper.addLoginCredential(pass,title);
                Toast.makeText(getContext(),"Pin saved",Toast.LENGTH_LONG).show();
                fragmentActionListener.onBackActionPerformed();


            }
            else{
                Toast.makeText(getContext(),"Pin not match",Toast.LENGTH_LONG).show();


            }
        }

    }



    private boolean validate(String pin1,String pin2) {

        if (pin1.length()<4) {
            editPin.setError("should be 4 digit");
            return false;
        }
        else if (TextUtils.isEmpty(pin2)) {
            editConfirm.setError("confirm pin");
            return false;

        }
        return true;

    }
}
