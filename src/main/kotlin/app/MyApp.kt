package app

import javafx.stage.Stage
import tornadofx.App
import view.MyView

class MyApp : App(MyView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isResizable = false
        stage.width = 250.0
    }
}