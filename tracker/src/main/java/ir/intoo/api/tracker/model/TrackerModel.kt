package ir.intoo.api.tracker.model

class TrackerModel : Profile() {
    var latitude = 0.0
    var longitude = 0.0
    var altitude = 0.0
    var speed = 0.0F
    var time = 0L
    var networkName = ""
}