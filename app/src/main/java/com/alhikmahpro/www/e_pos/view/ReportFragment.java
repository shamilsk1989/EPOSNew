package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {


    @BindView(R.id.textMonth)
    TextView textMonth;
    @BindView(R.id.textStaff)
    TextView textStaff;
    @BindView(R.id.textCustomer)
    TextView textCustomer;
    Unbinder unbinder;

    FragmentActionListener fragmentActionListener;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        unbinder = ButterKnife.bind(this, view);
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

    @OnClick({R.id.textMonth, R.id.textStaff, R.id.textCustomer})
    public void onViewClicked(View view) {
        Bundle bundle=new Bundle();
        switch (view.getId()) {
            case R.id.textMonth:
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,1);
                fragmentActionListener.onActionPerformed(bundle);
                break;
            case R.id.textStaff:
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,2);
                fragmentActionListener.onActionPerformed(bundle);
                break;
            case R.id.textCustomer:
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,3);
                fragmentActionListener.onActionPerformed(bundle);
                break;
        }
    }






}
