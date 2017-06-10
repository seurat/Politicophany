TEAM MEMBERS : 

Product/Project Manager: Ariel Winton
QA/Architecture: Nnamdi Okeke
Architects/Developers: Rani Aljondi, James O’Brien

EMAIL ADDRESSES :
Ariel Winton (Project/Product manager) : winton.a@husky.neu.edu
James O’Brien (Developer) : obrien.ja@husky.neu.edu
Nnamdi Okeke (QA/Architect) : okeke.n@husky.neu.edu
Rani Aljondi (Developer/Architect) : regress.arg@gmail.com


INSTRUCTIONS : 

- Open Terminal
- Navigate to the “main” directory in the “src” folder of the project
- Compile SignalMatcher.java by typing “javac SignalMatcher.java”
- Navigate to the directory containing the dan script.
- Change permissions on the dan script by typing “$chmod a+x ./dan”
- Run the dan script with parameters of the locations of the files 
or directories to be compared syntax below :

    ./dan -f <pathname> -f <pathname>
    ./dan -d <pathname> -d <pathname>
    ./dan -f <pathname> -d <pathname>
    ./dan -d <pathname> -f <pathname>



THIRD PARTY SOFTWARE :

-FFT Code : 
Copied from site: http://www.developer.com/java/other/article.php/3380031/Spectrum-Analysis-using-Java-Sampling-Frequency-Folding-Frequency-and-the-FFT-Algorithm.htm
Permission obtained from Richard Baldwin on October 9th, 2014
"Permission is granted.

Permission obtained from William Clinger on October 9th, 2014:

Please remember to acknowledge this use of Professor Baldwin's software in your README file, 
along with Baldwin's notice giving you permission to copy the code and to use, along with a 
statement of the fact that I gave you permission to use that software for your semester project 
on today's date.

The signature of its class is public class ForwardRealToComplexFFT01.
-LAME : CCIS Machines.
-oggdec : CCIS Machines.
