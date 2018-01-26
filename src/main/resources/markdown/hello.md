{% include header.md %}

### This is a simple sample

This is a *Sparta*

{% include footer.html %}

{{info title="This is a information block"}} 
This is just *plain content* 
{{/info}}

{{warning title="This is a warning block"}}
 1. First ordered list item
 2. Another item
 
 Emphasis, aka italics, with *asterisks* or _underscores_.
 
 Strong emphasis, aka bold, with **asterisks** or __underscores__.
 
 Combined emphasis with **asterisks and _underscores_**.
{{/warning}}

```javascript
var s = "JavaScript syntax highlighting";
alert(s);
```

{{note}}
# H1
## H2
### H3
#### H4
##### H5
###### H6
{{/note}}

day|time|spent
:---|:---:|--:
nov. 2. tue|10:00|4h 40m
nov. 3. thu|11:00|4h
nov. 7. mon|10:20|4h 20m
total:|| **13h**

Here's our logo (hover to see the title text):

Inline-style: 
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")