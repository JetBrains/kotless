package io.kotless


/** Type of scheduled event -- either user one [CloudwatchEventType.General] or autowarm [CloudwatchEventType.Autowarm] */
enum class CloudwatchEventType(val prefix: String) {
    General("general"),
    Autowarm("autowarm")
}
