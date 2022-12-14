package com.example.modernpoopz.List

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modernpoopz.*
import com.example.modernpoopz.Map.MapFragment
import com.example.modernpoopz.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var toiletAdapter: ToiletAdapter? = null
    private var _binding: FragmentListBinding? = null
    private var databaseHelper: DatabaseHelper? = null
    private var selectedFilters: ArrayList<String> = arrayListOf()
    private var currentSearchText: String = ""
    private var allButton: Button? = null
    private var freeButton: Button? = null
    private var maleButton: Button? = null
    private var femaleButton: Button? = null
    private var disabledButton: Button? = null
    private var babyButton: Button? = null
    private var searchView: SearchView? = null
    var toiletList: ArrayList<Toilet>? = arrayListOf<Toilet>()


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        databaseHelper = DatabaseHelper(requireContext(),null)

        return root
    }

    private fun storagePermission(): Boolean{
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )
        } == PackageManager.PERMISSION_GRANTED

    }


    private fun getToilets(){
        if(storagePermission()){
            this.activity?.let {
                Toilets.getToiletsFromApi(it)
                toiletList = databaseHelper?.getToilets()
            }
        }else{
            toiletList= Toilets.getToiletsWithoutPermission()
        }
        toiletAdapter?.setToilets(toiletList!!)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recipe_list_view)
        recyclerview?.layoutManager = LinearLayoutManager(view.context)

        toiletAdapter = ToiletAdapter()
        getToilets()

        recyclerview?.adapter = toiletAdapter

        toiletAdapter?.onItemClick = {toilet ->
            val intent = Intent(context, DetailView::class.java)
            intent.putExtra("straat", toilet.properties.STRAAT)
            intent.putExtra("huisnummer", toilet.properties.HUISNUMMER)
            intent.putExtra("postcode", toilet.properties.POSTCODE)
            intent.putExtra("betalend", toilet.properties.BETALEND)
            intent.putExtra("disabled", toilet.properties.INTEGRAAL_TOEGANKELIJK)
            intent.putExtra("target", toilet.properties.DOELGROEP)
            intent.putExtra("description", toilet.properties.OMSCHRIJVING)
            intent.putExtra("category", toilet.properties.CATEGORIE)
            intent.putExtra("extra_informatie", toilet.properties.EXTRA_INFO_PUBLIEK)
            intent.putExtra("openingsuren", toilet.properties.OPENINGSUREN_OPM)
            intent.putExtra("contactgegevens", toilet.properties.CONTACTGEGEVENS)

            if(toilet.geometry.coordinates!![0] != null && toilet.geometry.coordinates[1]!! != null){
                intent.putExtra("lat", toilet.geometry.coordinates[0])
                intent.putExtra("long", toilet.geometry.coordinates[1])
            }
            context?.startActivity(intent)
        }

        allButton = view.findViewById(R.id.allFilter)
        allButton?.setOnClickListener {
            allFilterTapped(view)
        }

        freeButton = view.findViewById(R.id.freeFilter)
        freeButton?.setOnClickListener {
            freeFilterTapped(view)
        }

        maleButton = view.findViewById(R.id.maleFilter)
        maleButton?.setOnClickListener {
            maleFilterTapped(view)
        }

        femaleButton = view.findViewById(R.id.femaleFilter)
        femaleButton?.setOnClickListener {
            femaleFilterTapped(view)
        }

        disabledButton = view.findViewById(R.id.disabledFilter)
        disabledButton?.setOnClickListener {
            disabledFilterTapped(view)
        }

        babyButton = view.findViewById(R.id.babyFilter)
        babyButton?.setOnClickListener {
            babyFilterTapped(view)
        }

        unSelectAllFilterButtons()
        lookSelected(allButton)
        selectedFilters.add("all")
        initSearchWidgets()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSearchWidgets(){
        searchView = view?.findViewById(R.id.searchViewField)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                currentSearchText = newText
                val filterToiletsList = arrayListOf<Toilet>()
                for (toilet in toiletList!!){
                    val street = toilet.properties.STRAAT?.lowercase()
                    val postcode = toilet.properties.POSTCODE

                    if(street?.contains(newText.lowercase()) == true || postcode.toString().contains(newText.lowercase())){

                        if(selectedFilters.contains("all")){
                            if(!filterToiletsList.contains(toilet)){
                                filterToiletsList.add(toilet)
                            }
                        }

                        else{
                            for(filter in selectedFilters){
                                var tempProp: String? = null
                                when (filter) {
                                    "nee_freefilter" -> {
                                        tempProp = toilet.properties.BETALEND + "_freefilter"
                                    }
                                    "man", "vrouw" -> {
                                        tempProp = toilet.properties.DOELGROEP
                                    }
                                    "ja_disabledfilter" -> {
                                        tempProp = toilet.properties.INTEGRAAL_TOEGANKELIJK + "_disabledfilter"
                                    }
                                    "ja_babyfilter" -> {
                                        tempProp = toilet.properties.LUIERTAFEL + "_babyfilter"
                                    }
                                }
                                if(tempProp?.lowercase()?.contains(filter) == true){
                                    if(!filterToiletsList.contains(toilet)){
                                        filterToiletsList.add(toilet)
                                    }
                                }
                            }

                        }
                    }
                }

                toiletAdapter?.setToilets(filterToiletsList)

                return false
            }
        })
    }

    private fun lookSelected(parsedButton:Button?){
        parsedButton?.alpha = 1.0F
    }

    private fun lookUnSelected(parsedButton: Button?){
        parsedButton?.alpha = 0.5F
    }

    private fun unSelectAllFilterButtons(){
        lookUnSelected(freeButton)
        lookUnSelected(maleButton)
        lookUnSelected(femaleButton)
        lookUnSelected(disabledButton)
        lookUnSelected(babyButton)
    }

    private fun filterList(status: String){

        if(!selectedFilters.contains(status)){
            selectedFilters.add(status)
        }

        val filterToiletsList = arrayListOf<Toilet>()
        //val toiletList = databaseHelper?.getToilets()

        for (toilet in toiletList!!){

            val street = toilet.properties.STRAAT?.lowercase()
            val postcode = toilet.properties.POSTCODE

            for(filter in selectedFilters){
                var tempProp: String? = null
                when (filter) {
                    "nee_freefilter" -> {
                        tempProp = toilet.properties.BETALEND + "_freefilter"
                    }
                    "man", "vrouw" -> {
                        tempProp = toilet.properties.DOELGROEP
                    }
                    "ja_disabledfilter" -> {
                        tempProp = toilet.properties.INTEGRAAL_TOEGANKELIJK + "_disabledfilter"
                    }
                    "ja_babyfilter" -> {
                        tempProp = toilet.properties.LUIERTAFEL + "_babyfilter"
                    }
                }
                if(tempProp?.lowercase()?.contains(filter) == true){
                    if (currentSearchText == ""){
                        if(!filterToiletsList.contains(toilet)){
                            filterToiletsList.add(toilet)
                        }
                    }
                    else{
                        if(street?.contains(currentSearchText.lowercase()) == true || postcode.toString().contains(currentSearchText.lowercase())){
                            if(!filterToiletsList.contains(toilet)){
                                filterToiletsList.add(toilet)
                            }
                        }
                    }
                }
            }
        }

        toiletAdapter?.setToilets(filterToiletsList)

    }

    private fun allFilterTapped(view: View) {
        selectedFilters.clear()
        selectedFilters.add("all")
        searchView?.setQuery("",false)
        searchView?.clearFocus()

        unSelectAllFilterButtons()
        lookSelected(allButton)
        getToilets()
    }
    private fun freeFilterTapped(view: View) {
        lookSelected(freeButton)
        lookUnSelected(allButton)
        selectedFilters.remove("all")
        filterList("nee_freefilter")
    }
    private fun maleFilterTapped(view: View) {
        lookSelected(maleButton)
        lookUnSelected(allButton)
        selectedFilters.remove("all")
        filterList("man")
    }
    private fun femaleFilterTapped(view: View) {
        lookSelected(femaleButton)
        lookUnSelected(allButton)
        selectedFilters.remove("all")
        filterList("vrouw")
    }
    private fun disabledFilterTapped(view: View) {
        lookSelected(disabledButton)
        lookUnSelected(allButton)
        selectedFilters.remove("all")
        filterList("ja_disabledfilter")
    }
    private fun babyFilterTapped(view: View) {
        lookSelected(babyButton)
        lookUnSelected(allButton)
        selectedFilters.remove("all")
        filterList("ja_babyfilter")
    }



}