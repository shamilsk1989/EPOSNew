package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
public class PrintSettingsFragment extends Fragment {

    FragmentActionListener fragmentActionListener;
    @BindView(R.id.textpaper)
    TextView textpaper;
    @BindView(R.id.textprinter)
    TextView textprinter;
    Unbinder unbinder;
    Toolbar toolbar;


    public PrintSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_print_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Print Settings");
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

    @OnClick({R.id.textpaper, R.id.textprinter})
    public void onViewClicked(View view) {
        Bundle bundle=new Bundle();
        switch (view.getId()) {

            case R.id.textpaper:
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,1);
                fragmentActionListener.onActionPerformed(bundle);
                break;
            case R.id.textprinter:
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,2);
                fragmentActionListener.onActionPerformed(bundle);
                break;
                default:break;
        }
    }
}
