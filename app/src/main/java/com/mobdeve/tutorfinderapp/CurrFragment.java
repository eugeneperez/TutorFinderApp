package com.mobdeve.tutorfinderapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<TuteeList> currentTutees = new ArrayList<>();
    private ArrayList<TuteeList> reqTutees = new ArrayList<>();

    public CurrFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrFragment newInstance(String param1, String param2) {
        CurrFragment fragment = new CurrFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateFragment(){
        TutorHomePage activity = (TutorHomePage) getActivity();
        currentTutees = activity.getCurTuteesList();
        reqTutees = activity.getReqTuteesList();
        Log.d("Tuteeslist", "onCreateView: curr "+currentTutees+" req "+reqTutees);
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
        // Inflate the layout for this fragment
        CountDownTimer count = new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                TutorHomePage activity = (TutorHomePage) getActivity();
                currentTutees = activity.getCurTuteesList();
                reqTutees = activity.getReqTuteesList();
                Log.d("Tuteeslist", "onCreateView: curr "+currentTutees+" req "+reqTutees);
            }
        };

        count.start();
        return inflater.inflate(R.layout.fragment_curr, container, false);
    }
}