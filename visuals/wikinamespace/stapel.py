"""
Hatching (pattern filled polygons) is supported currently in the PS,
PDF, SVG and Agg backends only.
"""
import matplotlib.pyplot as plt
from matplotlib.patches import Ellipse, Polygon
import numpy as np

randomDists = [" ", "en","fi", "he","ja","ko","nl"]


       
       
# load data into array

fig = plt.figure()
ax1 = fig.add_subplot(121)
ax1.set_title('Preselection')
ax1.bar(range(1,5), range(1,5), color='red', edgecolor='black', hatch="/")
ax1.bar(range(1,5), [6] * 4, bottom=range(1,5), color='blue', edgecolor='black', hatch='//')
ax1.set_xticks([1.5,2.5,3.5,4.5])

a = [ 1.5, 0  ]

ax2 = fig.add_subplot(122)
ax2.set_title('Linked pages')
bars = ax2.bar(range(1, 5), range(1,5), color='yellow', ecolor='black') + \
       ax2.bar(range(1, 5), [6] * 2, bottom=range(1,5), color='green', ecolor='black') + \
       ax2.bar(range(1, 5), [6] * 2, bottom=range(1,5), color='green', ecolor='black')
    
    
    
xtickNames = plt.setp(ax2, xticklabels=np.repeat(randomDists, 1))
plt.setp(xtickNames, rotation=0, fontsize=12)

plt.show()
