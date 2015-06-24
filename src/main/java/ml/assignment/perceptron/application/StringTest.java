package ml.assignment.perceptron.application;

public class StringTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int count=1;
		String testString="abcczz";
		int k;
		StringBuilder outString = new StringBuilder();
		for(int i=0; i<testString.length()-1;i++)
		{
			k=i+1;
			if(testString.charAt(i)==testString.charAt(k))
			{
				count++;
			}
			else
			{
				outString.append(String.valueOf(testString.charAt(i))+count);
				count=1;
			}
		}
		outString.append(String.valueOf(testString.charAt(testString.length()-1))+count);
		System.out.println(outString);

	}

}
