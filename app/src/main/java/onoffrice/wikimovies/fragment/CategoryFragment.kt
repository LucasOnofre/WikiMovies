package onoffrice.wikimovies.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import onoffrice.wikimovies.R

class CategoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

       var container = inflater.inflate(R.layout.fragment_category, container, false)

        return container
    }


}
