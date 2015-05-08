int CH1,CH2;

//X axis is rotational / steering left right
//Y axis is speed / driving foreward backward
//both ranging from -6 to 6
if(((x<-2)||(x>2))&&((y<2)&&(y>-2)) ) { //X only
    CH1= x; //rotate
    CH2=-x;
} else if ((x==0)&&(y!=0)) {
    CH1= y; //drive straight
    CH2= y; 
} else if ((x==6)&&(y==6)) { // 6 equals full throttle
    CH1= y; 
    CH2=-2; 
} else if ((x==-6)&&(y==6)) { 
    CH1=-2; 
    CH2= y; 
} else if ((x==6)&&(y==-6)) { 
    CH1= y; 
    CH2= 2; 
} else if ((x==-6)&&(y==-6)) { 
    CH1= 2; 
    CH2= y; 
} else if (abs(x)<abs(y)) {
    if(y<0) {
        if (x<-1) { 
            CH1=y+(2+abs(x)); 
            CH2=y; 
        } else if (x>1) { 
            CH1=y; 
            CH2=y+(2+abs(x)); 
        } else { 
            CH1=y; 
            CH2=y; 
        } 
    } else if (y>0) {
        if(x<-1) { 
            CH1=y-(2+abs(x)); 
            CH2=y; 
        } else if(x>1) { 
            CH1=y; 
            CH2=y-(2+abs(x)); 
        } else { 
            CH1=y; 
            CH2=y; 
        } 
    } 
} else if(abs(x)>=abs(y)) {
    if(y<0) {
        if(x<0) { 
            CH1=1; 
            CH2=y; 
        } else if(x>0) { 
            CH1=y; 
            CH2=1; 
        } 
    } else if(y>0) {
        if(x<0) { 
            CH1=-1; 
            CH2=y; 
        } else if(x>0) { 
            CH1=y; 
            CH2=-1; 
        } 
    } 
}