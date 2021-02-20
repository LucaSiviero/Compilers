#include<stdio.h>
#include<string.h>

float add(float a,float b);

float sub(float a,float b);

float mul(float a,float x);

float div(float a,float b);

float div(float a,float b);

void visit();
int main() {
	int operation;
	int continua = 1;
	float a;
	float b;
	float result = 3.2;
for(x = 0;x < 1;x = x + 1;
){int yy = 1;
x = 3.1;
}	printf("Benvenuto\n");

while (continua == 1) {
	printf("Scegli l'operazione aritmetica da effettuare: 1: +, 2: -, 3 : *, 4 : / ");
	scanf("%d", &operation);
	printf("Inserisci i due operandi da calcolare ");
	scanf("%f", &a);
	scanf("%f", &b);
if(operation == 1){
result = add(a,b);
}
else if(operation == 2){
result = sub(a,b);
}
else if(operation == 3){
result = mul(a,b);
}
else if(operation == 4){
result = div(a,b);
}
	printf("Il risultato è : ");
	printf("%f", result);
	printf("Vuoi continuare? 1 : si, 0 : no ");
	scanf("%d", &continua);

}
return 0;
}
float add (float a,float b){
}
float sub (float a,float b){
}
float mul (float a,float x){
}
float div (float a,float b){
}
float div (float a,float b){
}
void visit (){
}
