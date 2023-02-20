function applyCSS(el) {
  var head = document.getElementsByTagName('HEAD')[0];
  var link = document.createElement('link');
  link.rel = 'stylesheet';
  link.href = el;
  head.appendChild(link);
}

function getLocation(setview) {
  $.getJSON( "mylocation.json", function( data ) {
    //console.log("getting data.");
    //console.log(data);
    // remove previous marker
    for (var i = 0; i < markers.length; i++) {
      map.removeLayer(markers[i]);

    }
    markers = []

    // add new one
    var currentMarker = L.marker([data["lat"], data["lon"]], {icon: positionIcon});
    markers.push(currentMarker);
    currentMarker.addTo(map);

    // set zoom
    if(setview) {
      map.setView([data["lat"], data["lon"]], 15);
    }

    // set date
    var date = new Date(data["timestamp"]);
    var hours = date.getHours();
    var minutes = "0" + date.getMinutes();
    var seconds = "0" + date.getSeconds();
    var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
    document.getElementById("update-time").innerHTML = date;
  });
}

// set up icon
var positionIcon = L.icon({
    iconUrl: 'media/map-icon.png',
    iconSize:     [20, 20], // size of the icon
    iconAnchor:   [16, 16], // point of the icon which will correspond to marker's location
});

// create map
var map = L.map('map');
L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
	maxZoom: 18,
	attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' +
		'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
	id: 'mapbox/streets-v11',
	tileSize: 512,
	zoomOffset: -1,
  zoomControl: false
}).addTo(map);
map.dragging.disable();
map.doubleClickZoom.disable();
map.touchZoom.disable();
map.scrollWheelZoom.disable();
map.boxZoom.disable();
map.keyboard.disable();
map.setView([46.005798450776226, 6.693815444863306], 15);
var currentMarker = L.marker([46.005798450776226, 6.693815444863306], {icon: positionIcon});
currentMarker.addTo(map);
$(".leaflet-control-zoom").css("visibility", "hidden");

// recenter map each time
map.on('zoomend', function() {
    if (markers != null && markers.length > 0) {
      map.panTo(markers[0].getLatLng());
    }
});




var markers = [];
/*
getLocation(true);
setInterval(function() {
  getLocation(true);
}, 30000);
*/
