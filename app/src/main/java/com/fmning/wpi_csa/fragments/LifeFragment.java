package com.fmning.wpi_csa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.http.WCUtils;

//import com.loopj.android.http.


public class LifeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public LifeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LifeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LifeFragment newInstance(String param1, String param2) {
        Log.i("d", "initttttttttttt" + param1);
        LifeFragment fragment = new LifeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.mParam1 = param1;
        return fragment;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/
    ImageView iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_life, container, false);
        iv = (ImageView) view.findViewById(R.id.testImg);
        TextView tv = (TextView) view.findViewById(R.id.helloText);
        tv.setText(this.mParam1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("haha", "mmmmmmm" + (WCUtils.isNetworkAvailable() ? "available" : "no internet"));
//                //iv.setImageResource(R.drawable.life_tab);
//                AsyncHttpClient client = new AsyncHttpClient();
//                RequestParams params = new RequestParams();
//                params.put("version", "1.00.001");
//                params.put("id", 1);
//
//                client.get("https://wcservice.fmning.com/get_image", params, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
//                        iv.setImageBitmap(image);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        Log.d("haha", "111mmmmmmm");
//                    }
//                });
            };

                //client.get("", params, new )
                /*client.get("http://wc.fmning.com/get_version_info", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                        // Handle resulting parsed JSON response here
                        Log.d("haha", response.toString());
                    }



                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.d("haha", res);
                    }
                });*/

        });

        return view;
    }


}
