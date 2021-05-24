package com.mobdeve.tutorfinderapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    private RecyclerView rv_curr;
    private CurrentTutorsAdapter adapter = new CurrentTutorsAdapter(currentTutees);

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

    public void refreshFragment(CurrentTutorsAdapter adapter){
        updateFragment();
        CountDownTimer count = new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                adapter.notifyDataSetChanged();
            }
        };
        count.start();
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
        View view = inflater.inflate(R.layout.fragment_curr, container, false);
        updateFragment();

        CountDownTimer count = new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                rv_curr = (RecyclerView) view.findViewById(R.id.recycler_current);
                adapter = new CurrentTutorsAdapter(currentTutees);
                rv_curr.setAdapter(adapter);
                rv_curr.setLayoutManager(new LinearLayoutManager(c));
            }
        };
        count.start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshFragment(adapter);
    }

    public class CurrentTutorsAdapter extends RecyclerView.Adapter<CurrentTutorsAdapter.ViewHolder> {
        private ArrayList<TuteeList> tuteeList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text_name_rv;
            private TextView text_contact_rv;
            private ImageView image_profile_rv;
            private LinearLayout lil_butt;
            private LinearLayout tutors;
            private LinearLayout tutees;

            public ViewHolder(View view) {
                super(view);
                text_name_rv = view.findViewById(R.id.current_tutee_name);
                text_contact_rv = view.findViewById(R.id.current_tutee_contact);
                image_profile_rv = view.findViewById(R.id.current_tutee_image);
                lil_butt = view.findViewById(R.id.tutee_lil_butt);
                tutors = view.findViewById(R.id.tutors);
                tutees = view.findViewById(R.id.tutees);
            }
        }

        public CurrentTutorsAdapter(ArrayList<TuteeList> tuteeList) {
            this.tuteeList = tuteeList;
        }

        @NonNull
        @Override
        public CurrentTutorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View resultView = inflater.inflate(R.layout.rv_tutor_fragment, parent, false);

            CurrentTutorsAdapter.ViewHolder viewHolder = new CurrFragment.CurrentTutorsAdapter.ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CurrentTutorsAdapter.ViewHolder holder, int position) {
            TuteeList currentUser = tuteeList.get(position);

            Log.d("RREQ", "onBindViewHolder: ENTERED "+tuteeList.toString());

            holder.tutees.setVisibility(View.VISIBLE);
            holder.tutors.setVisibility(View.GONE);
            holder.lil_butt.setVisibility(View.GONE);

            holder.text_name_rv.setText(currentUser.getName());
            holder.text_contact_rv.setText(currentUser.getContact());
            String imgUri = currentUser.getImage_uri();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile_rv);
        }

        @Override
        public int getItemCount() {
            return tuteeList.size();
        }
    }
}