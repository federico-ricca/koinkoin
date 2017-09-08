/*************************************************************************** 
   Copyright 2017 Federico Ricca
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/

var n = 100, random = d3.randomNormal(0, .2), askPriceData = d3.range(n).map(
		random), bidPriceData = d3.range(n).map(random);
var svg = d3.select("svg"), margin = {
	top : 20,
	right : 20,
	bottom : 20,
	left : 40
}, width = +svg.attr("width") - margin.left - margin.right, height = +svg
		.attr("height")
		- margin.top - margin.bottom, g = svg.append("g").attr("transform",
		"translate(" + margin.left + "," + margin.top + ")");
var x = d3.scaleLinear().domain([ 0, n ]).range([ 0, width ]);
var yAxis = d3.scaleLinear().domain([ 0, 1 ]).range([ height, 0 ]);
var line = d3.line().x(function(d, i) {
	return x(i);
}).y(function(d, i) {
	return yAxis(d);
});
g.append("defs").append("clipPath").attr("id", "clip").append("rect").attr(
		"width", width).attr("height", height);
g.append("g").attr("class", "axis axis--x").attr("transform",
		"translate(0," + yAxis(0) + ")").call(d3.axisBottom(x));
g.append("g").attr("class", "axis axis--y").call(d3.axisLeft(yAxis));
g.append("g").attr("clip-path", "url(#clip)").append("path")
		.datum(askPriceData).attr("class", "ask-line").attr("d", line)
		.transition().duration(1000).ease(d3.easeLinear).on("start", tick);
g.append("g").attr("clip-path", "url(#clip)").append("path")
		.datum(bidPriceData).attr("class", "bid-line").attr("d", line)
		.transition().duration(1000).ease(d3.easeLinear).on("start", tick);

function tick() {
	// Redraw the line.
	d3.select(this).attr("d", line).attr("transform", null);
	// Slide it to the left.
	d3.active(this).attr("transform", "translate(" + x(-1) + ",0)")
			.transition().on("start", tick);
}