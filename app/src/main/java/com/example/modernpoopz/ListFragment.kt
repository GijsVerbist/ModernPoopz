package com.example.modernpoopz

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modernpoopz.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var toilet: Toilet? = null
    private var toiletAdapter: ToiletAdapter? = null
    private var _binding: FragmentListBinding? = null
    private var databaseHelper: DatabaseHelper? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //toiletAdapter = Toilet.getToiletsFromFile("toilets.json", this)
        databaseHelper = DatabaseHelper(requireContext(),null)


        return root
    }

    fun getToilets(){

        val toiletList = databaseHelper?.getToilets()

        println("get toilets " + toiletList?.count().toString())

        toiletAdapter?.setToilets(toiletList!!)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recipe_list_view)
        recyclerview?.layoutManager = LinearLayoutManager(view.context)

        toiletAdapter = ToiletAdapter()
        recyclerview?.adapter = toiletAdapter

        getToilets()

    }

}