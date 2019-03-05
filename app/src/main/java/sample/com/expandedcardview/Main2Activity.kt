package sample.com.expandedcardview

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import widget.com.expandablelayout.ExpandableLayout
import widget.com.expandedcardview.R

class Main2Activity : AppCompatActivity() {
    lateinit var container: LinearLayout
    lateinit var expandableLayout: ExpandableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        container = findViewById(R.id.container)
        expandableLayout = findViewById(R.id.expandable)
        addExpandable()
        addCustomExpandable()
        setListener()
    }

    private fun addExpandable() {
        var expandableLayout = ExpandableLayout(this)
        expandableLayout.setDefaultHeaderTitle("Added By Java")
        expandableLayout.setDefaultContentTitle("Content xxx xxxxxxxxx xxxxxxxx xxxxxx xxx")
        expandableLayout.setDefaultContentTextColor(Color.RED)
        expandableLayout.setArrowDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_down))

        container.addView(expandableLayout)
    }

    private fun addCustomExpandable() {
        var expandableLayout = ExpandableLayout(this)
        expandableLayout.setHeaderLayout(R.layout.layout_expandable_header)
        expandableLayout.setContentLayout(R.layout.layout_expandable_content)

        container.addView(expandableLayout)
    }

    private fun setListener() {
        expandableLayout.setOnExpandedListener { v, isExpanded ->
            Toast.makeText(this@Main2Activity
                    , "isExpanded== " + isExpanded, Toast.LENGTH_SHORT).show()
        }
    }


}
