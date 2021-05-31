package com.mobdeve.tutorfinderapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReqFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReqFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<TuteeList> currentTutees = new ArrayList<>();
    private ArrayList<TuteeList> reqTutees = new ArrayList<>();

    private RecyclerView rv_req;
    private ReqTutorsAdapter adapter;

    public ReqFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReqFragment.
     */
    public static ReqFragment newInstance(String param1, String param2) {
        ReqFragment fragment = new ReqFragment();
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
        final FragmentActivity c = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_req, container, false);
        updateFragment();
        CountDownTimer count = new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                rv_req = (RecyclerView) view.findViewById(R.id.recycler_req);
                adapter = new ReqTutorsAdapter(reqTutees);
                rv_req.setAdapter(adapter);
                rv_req.setLayoutManager(new LinearLayoutManager(c));
            }
        };
        count.start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class ReqTutorsAdapter extends RecyclerView.Adapter<ReqTutorsAdapter.ViewHolder> {
        private ArrayList<TuteeList> tuteeList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text_name_rv;
            private TextView text_contact_rv;
            private ImageView image_profile_rv;
            private Button button_accept;
            private Button button_decline;
            private LinearLayout lil_sta;
            private LinearLayout tutors;
            private LinearLayout tutees;

            public ViewHolder(View view) {
                super(view);
                text_name_rv = view.findViewById(R.id.current_tutee_name);
                text_contact_rv = view.findViewById(R.id.current_tutee_contact);
                image_profile_rv = view.findViewById(R.id.current_tutee_image);
                button_accept = view.findViewById(R.id.tutee_accept_btn);
                button_decline = view.findViewById(R.id.tutee_decline_btn);
                lil_sta = view.findViewById(R.id.tutor_lil_sta);
                tutors = view.findViewById(R.id.tutors);
                tutees = view.findViewById(R.id.tutees);
            }
        }

        public ReqTutorsAdapter(ArrayList<TuteeList> tuteeList) {
            this.tuteeList = tuteeList;
        }

        @NonNull
        @Override
        public ReqTutorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View resultView = inflater.inflate(R.layout.rv_tutor_fragment, parent, false);

            ReqTutorsAdapter.ViewHolder viewHolder = new ReqTutorsAdapter.ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ReqTutorsAdapter.ViewHolder holder, int position) {
            TuteeList currentUser = tuteeList.get(position);

            holder.lil_sta.setVisibility(View.GONE);
            holder.tutees.setVisibility(View.VISIBLE);
            holder.tutors.setVisibility(View.GONE);

            holder.text_name_rv.setText(currentUser.getName());
            holder.text_contact_rv.setText(currentUser.getContact());
            String imgUri = currentUser.getImage_uri();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile_rv);

            holder.button_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TutorHomePage activity = (TutorHomePage) getActivity();
                    activity.addToCurrent(currentUser.getEmail());
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(ReqFragment.this).attach(ReqFragment.this).commit();

                }
            });
            holder.button_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TutorHomePage activity = (TutorHomePage) getActivity();
                    activity.declineTutee(currentUser.getEmail());
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(ReqFragment.this).attach(ReqFragment.this).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return tuteeList.size();
        }
    }

}