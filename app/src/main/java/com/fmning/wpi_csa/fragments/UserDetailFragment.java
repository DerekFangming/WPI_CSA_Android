package com.fmning.wpi_csa.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.UserDetailListAdapter;
import com.fmning.wpi_csa.helpers.Utils;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fangmingning
 * On 12/27/17.
 */

public class UserDetailFragment extends Fragment {

    private UserDetailListAdapter tableViewAdapter;

    public UserDetailFragment(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);
        Button backButton = (Button) view.findViewById(R.id.userDetailBack);
        Button saveButton = (Button) view.findViewById(R.id.userDetailSave);
        final RecyclerView tableView = (RecyclerView) view.findViewById(R.id.userDetailList);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tableViewAdapter.isValueChanged()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(null).setCancelable(false).setMessage(getActivity().getString(R.string.user_detail_discard_confirm))
                            .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            })
                            .setNegativeButton(getActivity().getString(R.string.no), null)
                            .show();
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        tableViewAdapter = new UserDetailListAdapter(getActivity(), new UserDetailListAdapter.UserDetailListListener() {
            @Override
            public void OnAddAvatarClicked() {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tableViewAdapter.isValueChanged()) {
                    List<String> userDetails = tableViewAdapter.getUserDetails();
                    String name = userDetails.get(0).trim();
                    String birthday = userDetails.get(1).trim();
                    String classOf = userDetails.get(2).trim();
                    String major = userDetails.get(3).trim();

                    if (name.equals("")) {
                        Utils.showAlertMessage(getActivity(), getString(R.string.name_empty_error));
                        return;
                    }

                    if (name.length() > 20) {
                        Utils.showAlertMessage(getActivity(), getString(R.string.name_too_long_error));
                        return;
                    }

                    try {
                        int year = Integer.parseInt(classOf);
                        if (String.valueOf(year).length() != 4) {
                            Utils.showAlertMessage(getActivity(), getString(R.string.year_format_error));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Utils.showAlertMessage(getActivity(), getString(R.string.year_format_error));
                        return;
                    }

                    if (major.length() > 10) {
                        Utils.showAlertMessage(getActivity(), getString(R.string.major_too_long_error));
                        return;
                    }

                    Utils.showLoadingIndicator(getActivity());

                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast toast = Toast.makeText(getActivity(), getActivity().getString(R.string.user_detail_no_change),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        tableView.setAdapter(tableViewAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if(resultCode == RESULT_OK){
            Uri selectedImage = imageReturnedIntent.getData();
            tableViewAdapter.setAvatarUri(selectedImage);
            tableViewAdapter.notifyItemChanged(1);
        }
    }
}
