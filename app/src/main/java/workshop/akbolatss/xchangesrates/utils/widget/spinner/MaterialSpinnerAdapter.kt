package workshop.akbolatss.xchangesrates.utils.widget.spinner

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class MaterialSpinnerAdapter(context: Context, layout: Int, val entries: List<Any>) :
    ArrayAdapter<Any>(context, layout, entries) {

    private val noActionFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = entries
            results.count = entries.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return noActionFilter
    }
}
