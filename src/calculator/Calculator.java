package calculator;

import java.util.*;

public class Calculator
{
	//单项式类
	private static class Monomial
	{
		//表达式中的元素
		Double coefficient;//系数
		HashMap<String, Integer> vars;//变量，保存为变量名->次数的映射

		Monomial(String monomial)
		{
			coefficient = 1.0;
			vars = new HashMap<String, Integer>();
			String var;//变量名
			int index;//指数

			if(monomial.charAt(0) == '-')//处理减号
			{
				coefficient = -1.0;
				monomial = monomial.substring(1);
			}
			else if(monomial.charAt(0) == '+')//处理加号
			{
				coefficient = 1.0;
				monomial = monomial.substring(1);
			}
			String[] factors = monomial.split("\\*|(?<=[0-9])(?=[A-Za-z])|(?<=[A-Za-z])(?=[0-9])");
			/*正常来讲，factor只能有以下几种情况：
			 *1、变量，如x，boo
			 *2、数字，如1, 12，15.2
			 *3、幂式，如x^3，value^5
			 */
			for (String factor : factors)
			{
				if(factor.matches("^[A-Za-z]+$"))//变量
					vars.put(factor,((vars.containsKey(factor) ? (vars.get(factor))+1 : 1)));
				else if(factor.matches("^[0-9]+(\\.[0-9]+)?$"))//数字
					coefficient *= Double.valueOf(factor);
				else if(factor.matches("^[A-Za-z]+\\^[0-9]+$"))//幂式
				{
					String[] pair = factor.split("(?<=[A-Za-z]+)\\^(?=[1-9][0-9]*)");
					if(pair.length == 1)
						;//TODO 异常处理
					else
					{
						if (pair[0].matches("^[A-Za-z]+$") && pair[1].matches("^[0-9]+$"))
						{	
							var = pair[0];
							index = Integer.valueOf(pair[1]);
							vars.put(var,((vars.containsKey(var) ? (vars.get(var))+index : index)));
						}
						else
							return;//TODO 异常处理
					}

				}
				else
					;//TODO 异常处理
			}
		}
		Monomial()
		{
			coefficient = 1.0;
			vars = new HashMap<String, Integer>();
		}

	}
	
	//合并同类项并进行输出
	static void print(ArrayList <Monomial> result)
	{
		//合并同类项
		HashMap <HashMap<String, Integer>,Monomial> map = new HashMap <HashMap<String, Integer>,Monomial>();
		for(int i=0; i<result.size(); i++)
		{
			Monomial monomial = result.get(i);
			if(map.containsKey(monomial.vars))
			{
				map.get(monomial.vars).coefficient += monomial.coefficient;
				result.remove(i);
				i--;
			}
			else
				map.put(monomial.vars, monomial);
		}
		/*这个部分用来删除系数为0的项，但是我不输出它就可以不用删了
		for(int i=0; i<result.size(); i++)
		{
			if(result.get(i).coefficient.equals(0.0))
			{
				result.remove(i);
				i--;
			}
		}*/
		//将结果输出
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
		System.out.println('\n');
	}

	//将字符串表达式转换成自定义数据类型
	static ArrayList<Monomial> expression(String input)
	{
		ArrayList <Monomial> exp = new ArrayList <Monomial> ();//表达式

		String fixedInput = input.replaceAll("(?<=[+\\-*^])\\s+(?=[+\\-*^])","");
		String[] monomials = fixedInput.split("(?=\\+|-)");
		for(String monomial : monomials)
			exp.add(new Monomial(monomial));
		print(exp);
		return exp;
	}
	//按照输入的值对表达式进行计算
	static void simplify(ArrayList <Monomial> exp, String input)
	{
		ArrayList <Monomial> result = new ArrayList <Monomial>();
		HashMap <String,Double> solves = new HashMap <String,Double>();

		//得到单个赋值表达式，进行处理，得到赋值表
		String[] assigns = input.substring(9).split("\\s+");
		for(String assign : assigns)
		{
			if(assign.length() == 0)
				continue;
			//else
			String[] temp = assign.split("=");
			if(temp.length == 1)
				;//TODO 异常处理
			else
			{
				if(temp[0].matches("^[A-Za-z]+$") && temp[1].matches("^[0-9]+(\\.[0-9]+)?$"))
				{
					if(!(solves.containsKey(temp[0])))
						solves.put(temp[0], (Double.valueOf(temp[1])));
					else
						;//TODO 异常处理
				}
				else
					;//TODO 异常处理	
			}
		}
		//对乘法进行运算
		for(int i=0; i<exp.size(); i++)
		{
			Monomial monomial = exp.get(i);//处理一个单项式
			result.add(new Monomial());
			result.get(i).coefficient = monomial.coefficient;//设置系数
			for(String var : monomial.vars.keySet())//逐个规定变量
			{
				if(solves.containsKey(var))
					result.get(i).coefficient *=  Math.pow(solves.get(var), exp.get(i).vars.get(var));
				else
					result.get(i).vars.put(var, monomial.vars.get(var));
			}
		}
		print(result);
	}
	//对表达式求导
	static void derivative(ArrayList <Monomial> exp, String input)
	{
		ArrayList <Monomial> result = new ArrayList <Monomial>();
		String assign = input.substring(4).replaceAll("\\s+","");
		if(!assign.matches("^[A-Za-z]+$"))
			return;//TODO 异常处理
		//else
		for(int i=0; i<exp.size(); i++)
		{
			Monomial monomial = exp.get(i);//处理一个单项式
			result.add(new Monomial());
			result.get(i).coefficient = monomial.coefficient;//设置系数
			if(!monomial.vars.containsKey(assign))
			{
				result.remove(i);
				i--;
			}
			else
			{
				for(String var: monomial.vars.keySet())//逐个规定变量
				{
					if((assign.equals(var)) && (exp.get(i).vars.get(var)>1))
					{
						result.get(i).coefficient *= exp.get(i).vars.get(var);
						result.get(i).vars.put(var, monomial.vars.get(var)-1);
					}
					else if(!assign.equals(var))
						result.get(i).vars.put(var, monomial.vars.get(var));
					else
						;//TODO 异常处理
				}
			}
		}
		print(result);
	}

	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		while(true)
		{
			System.out.print('5');
			System.out.println('\n');
			/*String input = scan.nextLine();
			if(input.equals("!"))
			{
				scan.close();
				System.exit(0);
			}
			else*/
			//{
			/*String temp = input.substring(9);
				String[] splitedInputs = temp.split("\\s+");
				for(String monomial:splitedInputs)
				{
					System.out.println(monomial);
				}*/
			//}
		}
		/*ArrayList <Monomial> exp;
		while(true)
		{
			if(scan.hasNextLine())
			{
				String input = scan.nextLine();
				if(input.matches("^[\\s]*[-]*[\\s]*{0,1}[A-Za-z0-9]+[A-Za-z0-9]*(([\\s]*)([*^])([\\s]*)([A-Za-z0-9]+))*(([\\s]*)([+-])([\\s]*)([A-Za-z0-9])+(([\\s]*)([*^])([\\s]*)([A-Za-z0-9]+))*)*"))//处理表达式
					exp = expression(input);
				else if(input.matches("!simplify[ A-Za-z0-9=]+$") )//处理运算命令
					simplify(exp,input);
				else if(input.matches("!d\\/d[\\sA-Za-z]+$"))//处理求导命令
					derivative(exp,input);
				else
					//TODO 异常
			}

		}*/
	}

}
