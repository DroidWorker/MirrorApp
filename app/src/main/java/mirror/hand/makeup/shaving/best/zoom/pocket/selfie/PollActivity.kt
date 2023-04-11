package mirror.hand.makeup.shaving.best.zoom.pocket.selfie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import mirror.hand.makeup.shaving.best.zoom.pocket.selfie.VM.MainViewModel

class PollActivity : AppCompatActivity() {
    private val userViewModel by viewModels<MainViewModel>()
    lateinit var votes : Map<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        votes = userViewModel.getVotes()
        val percents: MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
        var total = 0
        votes.forEach{
            total+=it.value
            when(it.key){
                "Shave"->{
                    percents["1"] = it.value
                }
                "Putting_on_makeup"->{
                    percents["2"] = it.value
                }
                "Putting_on_lenses"->{
                    percents["3"] = it.value
                }
                "Use_in_dark"->{
                    percents["4"] = it.value
                }
                "Brush_your_teeth"->{
                    percents["5"] = it.value
                }
                "Just_to_look"->{
                    percents["6"] = it.value
                }
            }
        }
        findViewById<ProgressBar>(R.id.pBar1).progress = (((percents["1"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar2).progress = (((percents["2"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar3).progress = (((percents["3"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar4).progress = (((percents["4"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar5).progress = (((percents["5"]!!)*100)/total)
        findViewById<ProgressBar>(R.id.pBar6).progress = (((percents["6"]!!)*100)/total)
    }

    fun onClick(v: View){
        val tv = v as ConstraintLayout
        when(tv.tag.toString()){
            "1"->{
                votes["Shave"]?.let { userViewModel.addVote("Shave", it+1) }
            }
            "2"->{
                votes["Putting_on_makeup"]?.let { userViewModel.addVote("Putting_on_makeup", it+1) }
            }
            "3"->{
                votes["Putting_on_lenses"]?.let { userViewModel.addVote("Putting_on_lenses", it+1) }
            }
            "4"->{
                votes["Use_in_dark"]?.let { userViewModel.addVote("Use_in_dark", it+1) }
            }
            "5"->{
                votes["Brush_your_teeth"]?.let { userViewModel.addVote("Brush_your_teeth", it+1) }
            }
            "6"->{
                votes["Just_to_look"]?.let { userViewModel.addVote("Just_to_look", it+1) }
            }
        }

        this.finish()
    }
}