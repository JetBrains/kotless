package io.kotless

@InternalAPI
/** Type of scheduled event -- either user one [ScheduledEventType.General] or autowarm [ScheduledEventType.Autowarm] */
enum class ScheduledEventType(val prefix: String) {
    General("general"),
    Autowarm("autowarm")
}
