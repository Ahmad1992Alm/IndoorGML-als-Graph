<?xml version="1.0" encoding="UTF-8"?>
<IndoorGML xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:xlink="http://www.w3.org/1999/xlink">
    <CellSpace gml:id="cs1">
        <gml:name>RoomA</gml:name>
    </CellSpace>
    <CellSpace gml:id="cs2">
        <gml:name>RoomB</gml:name>
    </CellSpace>
    <Transition gml:id="tr1">
        <connects xlink:href="#cs1"/>
        <connects xlink:href="#cs2"/>
    </Transition>
</IndoorGML>
