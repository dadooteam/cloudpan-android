package com.project_test.forsix;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment2 extends Fragment {
private Button enter;

    public SplashFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret=inflater.inflate(R.layout.fragment_splash_fragment2, container, false);
        enter= (Button) ret.findViewById(R.id.enter);
        final Intent intent=new Intent(getActivity(),ShowCloudFilesActivity.class);
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().finish();
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                thread.start();
            }
        });
        return ret;
    }

}
