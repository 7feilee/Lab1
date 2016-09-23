package calculator;

import java.util.*;

public class Calculator
{
	//����ʽ��
	private static class Monomial
	{
		//���ʽ�е�Ԫ��
		Double coefficient;//ϵ��
		HashMap<String, Integer> vars;//����������Ϊ������->������ӳ��
		
		Monomial(String monomial)
		{
			coefficient = 1.0;
			vars = new HashMap<String, Integer>();
			
			String var;//������
			int index;//ָ��
			
			if(monomial.charAt(0) == '-')//������
			{
				coefficient = -1.0;
				monomial = monomial.substring(1);
			}
			String[] factors = monomial.split("\\*|(?<=[0-9])(?=[A-Za-z])|(?<=[A-Za-z])(?=[0-9])");
			/*����������factorֻ�������¼��������
			 *1����������x��boo
			 *2�����֣���1, 12��15.2
			 *3����ʽ����x^3��value^5
			 */
			for (String factor : factors)
			{
				if(factor.matches("^[A-Za-z]+$"))//����
					vars.put(factor,(((vars.get(factor)==null) ? 0 : (vars.get(factor)))+1));
				else if(factor.matches("^[0-9]+(\\.[0-9]+)?$"))//����
					coefficient *= Double.valueOf(factor);
				else if(factor.matches("^[A-Za-z]+\\^[0-9]+$"))//��ʽ
				{
					String[] pair = factor.split("(?<=[A-Za-z]+)\\^(?=[1-9][0-9]*)");
					if(pair.length == 1)
						;//TODO �쳣����
					else
					{
						if (pair[0].matches("^[A-Za-z]+$") && pair[1].matches("^[0-9]+$"))
						{	
							var = pair[0];
							index = Integer.valueOf(pair[1]);
							vars.put(var,(((vars.get(var)==null) ? 0 : (vars.get(var)))+index));
						}
						else
							return;//TODO �쳣����
					}
					
				}
				else
					;//TODO �쳣����
			}
		}
		Monomial()
		{
			coefficient = 1.0;
			vars = new HashMap<String, Integer>();
		}
		
	}
	//���ַ������ʽת�����Զ�����������
	static ArrayList<Monomial> expression(String input)
	{
		ArrayList <Monomial> exp = new ArrayList <Monomial> ();//���ʽ
		//TODO ���ж����������� @������
		String fixedInput = input.replaceAll("(?<=[+\\-*^])\\s+(?=[+\\-*^])","");
		String[] monomials = fixedInput.split("(?=\\+|-)");
		for(String monomial : monomials)
			exp.add(new Monomial(monomial));
		return exp;
	}
	//���������ֵ�Ա��ʽ���м���
	static void simplify(ArrayList <Monomial> exp, String input)
	{
		ArrayList <Monomial> result = new ArrayList <Monomial>();
		String[] assigns = input.substring(9).split("\\s+");
		HashMap <String,Double> solves = new HashMap <String,Double>();
		Double tempcoe = 0.0;
		//�õ�������ֵ���ʽ�����д����õ���ֵ��
		for(String assign : assigns)
		{
			if(assign.length() == 0)
				continue;
			//else
			String[] temp = assign.split("=");
			if(temp.length == 1)
				;//TODO �쳣����
			else
			{
				if(temp[0].matches("^[A-Za-z]+$") && temp[1].matches("^[0-9]+(\\.[0-9]+)?$"))
				{
					if(!(solves.containsKey(temp[0])))
						solves.put(temp[0], (Double.valueOf(temp[1])));
					else
						;//TODO �쳣����
				}
				else
					;//TODO �쳣����	
			}
		}
		//�Գ˷���������
		for(int i=0; i<exp.size(); i++)
		{
			Monomial monomial = exp.get(i);//����һ������ʽ
			result.add(new Monomial());
			result.get(i).coefficient = monomial.coefficient;//����ϵ��
			for(String var : monomial.vars.keySet())//����涨����
			{
				if(solves.containsKey(var))
					result.get(i).coefficient *=  Math.pow(solves.get(var), exp.get(i).vars.get(var));
				else
					result.get(i).vars.put(var, monomial.vars.get(var));
			}
		}
		//�Լӷ���������
		//TODO 
		for(int i=0; i<result.size(); i++)
		{
			if((result.get(i).vars.isEmpty()) || (result.get(i).coefficient.equals(0.0)))
			{
				tempcoe += result.get(i).coefficient;
				result.remove(i);
				i--;
			}
		}
		//��������
		if(!tempcoe.equals(0.0))
			System.out.print(tempcoe);
		for(int i=0;i<result.size();i++)
		{
			Monomial monomial = result.get(i);
			if(monomial.coefficient < 0)
				System.out.print(monomial.coefficient);
			else if((monomial.coefficient > 0) && (i != 0))
				System.out.print("+" + monomial.coefficient);
			for(String var : monomial.vars.keySet())
			{
				for(int i1=0; i1<monomial.vars.get(var);i1++)
					System.out.print("*"+var);
			}
		}
	}
	//�Ա��ʽ��
	static void derivative(ArrayList <Monomial> exp, String input)
	{
		ArrayList <Monomial> result = new ArrayList <Monomial>();
        String assigns = input.substring(4).replaceAll("\\s+","");
        for(int i=0; i<exp.size(); i++)
        {
            Monomial monomial = exp.get(i);//����һ������ʽ
            result.add(new Monomial());
            result.get(i).coefficient = monomial.coefficient;//����ϵ��
            if(!monomial.vars.containsKey(assigns))
            {
            	result.remove(i);
            	i--;
            }
            else
            {
            	for(String var: monomial.vars.keySet())//����涨����
	            {
	                if(assigns.equals(var)&&exp.get(i).vars.get(var)>1)
	                {
	                	result.get(i).coefficient *= exp.get(i).vars.get(var);
	                    result.get(i).vars.put(var, monomial.vars.get(var)-1);
	                }
	                else if(!assigns.equals(var))
	                	result.get(i).vars.put(var, monomial.vars.get(var));
	            }
            }
        }
        
	}
	
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		while(true)
		{
			String input = scan.nextLine();
			if(input.equals("!"))
			{
				scan.close();
				System.exit(0);
			}
			else
			{
				Double test = -1.2;
				if(!(test == 1.2))
				{
					System.out.println("+" + test);
				}
				/*String temp = input.substring(9);
				String[] splitedInputs = temp.split("\\s+");
				for(String monomial:splitedInputs)
				{
					System.out.println(monomial);
				}*/
			}
		}
		/*ArrayList <Monomial> exp;
		while(true)
		{
			if(scan.hasNextLine())
			{
				String input = scan.nextLine();
				if(input.matches("^[\\s]*[-]*[\\s]*{0,1}[A-Za-z0-9]+[A-Za-z0-9]*(([\\s]*)([*^])([\\s]*)([A-Za-z0-9]+))*(([\\s]*)([+-])([\\s]*)([A-Za-z0-9])+(([\\s]*)([*^])([\\s]*)([A-Za-z0-9]+))*)*"))//������ʽ
					exp = expression(input);
				else if(input.matches("!simplify[ A-Za-z0-9=]+$") )//������������
					simplify(exp,input);
				else if(input.matches("!d\\/d[\\sA-Za-z]+$"))//����������
					derivative(exp,input);
				else
					//TODO �쳣
			}
			
		}*/
	}

}
