<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <script type='text/javascript' src='https://www.google.com/jsapi'></script>
    <script type='text/javascript'>
     google.load('visualization', '1', {'packages': ['geochart']});
     google.setOnLoadCallback(drawRegionsMap);

      function drawRegionsMap() {
        var data = google.visualization.arrayToDataTable([

          ['Country', 'Relevanz'],
          ['fr', 1.937 ],
          ['he', 5.162 ],
          ['de', 5.0 ],
          ['fa', 14.9],
          ['es', 10.4],
          ['az', 0.3],
          ['pl', 0.6],
          ['ml', 1.3],
          ['mn', 0.8],
          ['ko', 3.1],
          ['it', 1.3],
          ['jp', 8.5],
          ['vi', 0.01],
          ['ru', 14.0],
          ['pt', 0.7],
          ['gb', 6.8],
          ['us', 22.5],
          ['zh', 2.2]      
 
    
        ]);

        var options = { colors: ['#FF0000', '#00FF00'] };

        var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));
        chart.draw(data, options);
        
        
        
        
        
        
    };
    </script>
  </head>
  <body>
      <h1>Interwikilinks von einer Seite</h1>
      <h2>Apache Hadoop (en)</h2>
      <form action="chart1.jsp">
          Sprache : <input type="text"/><br/> 
          Seite : <input type="text"/> 
          <input type="submit" />
      </form>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
  </body>
</html>