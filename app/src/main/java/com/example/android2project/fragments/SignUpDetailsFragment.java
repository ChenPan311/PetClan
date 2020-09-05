package com.example.android2project.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android2project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public interface SignUpDetailsListener {
        void onFacebook(String screenName);
        void onGoogle(String screenName);
        void onNext(String screenName, String email, String password);
    }

    private SignUpDetailsListener listener;

    public SignUpDetailsFragment() {}

    // TODO: Rename and change types and number of parameters
    public static SignUpDetailsFragment newInstance() {
        SignUpDetailsFragment fragment = new SignUpDetailsFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SignUpDetailsListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement SignUpDetails Listener!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_details, container, false);

        final ImageView facebookBtn = rootView.findViewById(R.id.facebook_btn);
        final ImageView googleBtn = rootView.findViewById(R.id.google_btn);
        final Button nextBtn = rootView.findViewById(R.id.next_btn);

        final EditText emailEt = rootView.findViewById(R.id.email_et);
        final EditText passwordEt = rootView.findViewById(R.id.password_et);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFacebook("SignUpDetails");
                }
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGoogle("SignUpDetails");
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();

                    //TODO: Verify valid Email and password

                    listener.onNext("SignUpDetails", email, password);
                }
            }
        });

        return rootView;
    }
}