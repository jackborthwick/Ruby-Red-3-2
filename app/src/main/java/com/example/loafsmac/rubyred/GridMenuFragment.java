package com.example.loafsmac.rubyred;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridMenuFragment extends Fragment
        implements AdapterView.OnItemClickListener{

    private static final String KEY_BG_RESOURCE_ID = "key_bg_resource_id";
    private OnFragmentInteractionListener mListener;

    public GridMenuFragment() {
        // Required empty public constructor
    }

    public static GridMenuFragment newInstance(int backgroundResourceID) {
        GridMenuFragment gridMenuFragment = new GridMenuFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_BG_RESOURCE_ID, backgroundResourceID);
        gridMenuFragment.setArguments(args);
        return gridMenuFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridMenuAdapter = new GridMenuAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_grid_menu, null);

        GridView gridView = (GridView) view.findViewById(R.id.menu_grid_view);
        mGridMenuAdapter.addAll(this.mMenus);
        gridView.setAdapter(mGridMenuAdapter);
        gridView.setOnItemClickListener(this);
        return view;
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mOnClickMenuListener == null) {
            throw new IllegalArgumentException("Must implement setOnClickMenuListener");
        } else {
            mOnClickMenuListener.onClickMenu(mMenus.get(i), i);
        }
    }

    public void setupMenu(List<GridMenu> menus) {
        this.mMenus = menus;
    }

    private GridMenuAdapter mGridMenuAdapter;
    private List<GridMenu> mMenus = new ArrayList<>();
    private OnClickMenuListener mOnClickMenuListener;
    public void setOnClickMenuListener(OnClickMenuListener listener) {
        mOnClickMenuListener = listener;
    }

    public interface OnClickMenuListener {
        void onClickMenu(GridMenu gridMenu, int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
