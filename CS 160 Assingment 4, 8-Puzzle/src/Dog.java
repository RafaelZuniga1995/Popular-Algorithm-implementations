public class Dog {

	String name;
	int years;
	
	
	public Dog(String name, int years){
		this.name = name;
		this.years = years;
		
	}
	
	public void bark(){
		System.out.println("rawr");
		
	}
	
	public int howOld(){
		return years;
	}
	
	public String nameIs(){
		return name;
		
	}
	
	
	public static void main(String[] args){
		
		int[][] array = new int[5][5];
		System.out.println(array.length);
		
		
	}
	
	
}
