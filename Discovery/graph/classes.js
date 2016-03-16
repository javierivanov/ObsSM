//Show UCLA CS class dependencies (not complete)
$(document).ready(function() {
    var width = $(document).width();
    var height = $(document).height();
    var g = new Graph();
    g.edgeFactory.template.style.directed = true;
    
    for (i=0; i < data.length; i++)
    {
        g.addEdge(data[i][0], data[i][1], {  fill : "#56f" });
    }
    
    var layouter = new Graph.Layout.Spring(g);
    layouter.layout();
    var renderer = new Graph.Renderer.Raphael('canvas', g, width, height);
});