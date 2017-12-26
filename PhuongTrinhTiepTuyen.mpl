PhuongTrinhTiepTuyen := module()
	description "Module giải bài toán phương trình tiếp tuyến";
    option package;
	
	local MSet, F, fixLineEquation, printLatex, printRequest;
	
	export getK, getSolutions, createPoints, getEquations, findSolution, showSolution;
	
	MSet := {pttt, M, N, d, d1, k, kx, fx, x0, y0}; # biến chứa giá trị tương ứng là phuongtrinh, diqua, tiepxuc, songsong, vuonggoc, hesogoc, hesogoc(x), dothi, xtiepdiem, ytiepdiem.
	
	F := [
		[{d}, {k}, [k = `coeff(d, x, 1)`], "delta song song với d, tính được hệ số góc k"],
		[{d1}, {k}, [k = `-1 / coeff(d1, x, 1)`], "delta vuông góc với d1, tính được hệ số góc k"],
		[{M, kx}, {k}, [k = `getK(M, kx)`], "delta nhận M làm tiếp điểm, có phương trình hệ số góc kx, tính được hệ số góc k bằng cách thế hoành độ tiếp điểm x0 vào kx"],
		[{M, k}, {pttt}, [pttt = `getEquations(M, k)`], "delta nhận M làm tiếp điểm, có hệ số góc k, ta viết được Phương trình tiếp tuyến"],
		[{N, kx, fx}, {x0}, [x0=`getSolutions(N[2] = kx*(N[1] - x) + fx)`], "delta đi qua N (a, b), giải phương trình b = kx0(a - x0) + f(x0), tìm được x0"],
		[{fx}, {kx}, [kx = `diff(fx, x)`], "Từ fx, suy ra hệ số góc của delta: kx = f'(x)"],
		[{kx, k}, {x0}, [x0 = `getSolutions(kx = k)`], "Giải phương trình kx = k, ta được hoành độ tiếp điểm x0"],
		[{fx,x0},{y0, M}, [y0=`map(x->fx,x0)`, M = `createPoints(x0, y0)`], "Biết được hoành độ tiếp điểm x0, thế vào phương trình fx tính được tung độ tiếp điểm y0, Từ đó tìm được tiếp điểm M(x0, y0))"]
	];
	
	# Chuẩn hóa lại phương trình đường thẳng
	fixLineEquation := proc(hypothesis, vals)
		local Known, Values, v, eq, nx, ny;
		Known := hypothesis;
		Values := vals;
		if ({d, d1} intersect Known) = {} then # đề bài không chứa đường thẳng nào
			return Known, Values;
		end if;
		
		for v in Values do
			if (lhs(v) = d or lhs(v) = d1) then
				eq := parse(rhs(v));
				if not(indets(eq, name) subset {x, y}) then # phương trình chứa biến không hợp lệ
					return {}, {};
				end if;
				
				if type(eq, equation) then # phương trình đường thẳng chưa ở dạng chuẩn
					# chuẩn hóa
					eq := lhs(eq) - rhs(eq); # chuyển vế
					ny := coeff(eq, y, 1); # hệ số của y
					nx := coeff(eq, y, 0); # phần còn lại
					Values := Values minus {v};
					Values := Values union {lhs(v) = -nx / ny};
				end if;
			end if;
		end do;
		return Known, Values;
	end proc;
	
	# Giải phương trình eq, chỉ lấy nghiệm thực
    getSolutions := proc(eq) 
        local solutions, sol;

        solutions := {solve(eq, x)};
        
        # Loại các nghiệm phức
        for sol in solutions do
            if Im(sol) <> 0 then
                solutions := solutions minus {sol};
            end if;
        end do;
		if nops(solutions) > 1 then
			return [op(solutions)];
		end if;
		
		return op(solutions);
    end proc;
	
	# tạo ra danh sách điểm từ danh sách hoành độ x và tung độ y
	createPoints := proc(xList, yList)
		local i, result;
		if not(type(xList, list)) then
			return [xList, yList]; # chỉ có 1 điểm
		end if;
		
		result := [];
		for i from 1 to nops(xList) do
			result := [op(result), [xList[i], yList[i]]];
		end do;
		
		return result;
	end proc;
	
	# Tạo danh sách hệ số góc Từ danh sách tiếp điểm
	getK := proc(M, kx)
		local result, p;
		if not(type(M, listlist)) then # Chỉ có 1 điểm
			return subs({x=M[1]},kx);
		end if;	
		
		result := [];
		for p in M do
			result := [op(result), subs({x=p[1]},kx)];
		end do;
		return result;
	end proc;
	
	# Tạo ra danh sách phương trình tiếp tuyến từ danh sách tiếp điểm và danh sách hệ số góc
	getEquations := proc(M, k)
		local result, i;
		if not(type(M, listlist)) then # chỉ có 1 phương trình
			return (k*x -M[1] * k + M[2]);
		end if;
		
		result := [];
		for i from 1 to nops(M) do
			if not(type(k, list)) then
				result := [op(result), k*x -M[i][1] * k + M[i][2]];
			else
				result := [op(result), k[i]*x -M[i][1] * k[i] + M[i][2]];
			end if;
		end do;
		return result;
	end proc;
	
	# Tìm kiếm lời giải
	findSolution := proc(hypothesis, vals)
		local Known, Values, f, found, e, re, Solution, subSolution, v, message;
		Known, Values := fixLineEquation(hypothesis, vals);
		
		if Known = {} then
			message := "Bài toán không hợp lệ, hãy kiểm tra lại";
			return [], message;
		end if;
		
		Solution := [];
		message := "";
		while not(member(pttt, Known)) do
			found := false;
			for f in F do
				if (f[1] subset Known) and not(f[2] subset Known) then # sự kiện đã biết và có phát sinh sự kiện mới
					found := true;
					subSolution := [f[4]];
					v := {}; # tập các giá trị mới tìm được
					for e in f[3] do
						re := eval(subs(Values,parse(rhs(e))));
						if type(re, list) and nops(re) = 0 then # kết quả là 1 list rỗng
							found := false;
							message := "Bài toán gặp phải bước giải chứa 1 phương trình vô nghiệm.";
							for v in Values do # Loại bỏ 1 số biến lỗi đã tìm được trước đó
								if not(member(lhs(v), Known)) then
									Values := Values minus {v};
								end if;
							end do;
							break;
						end if;
						
						if not(member(lhs(e), Known)) then # giá trị của biến này chưa biết
							Values := {op(Values), lhs(e) = re};
							subSolution := [op(subSolution), lhs(e) = re];
						end if;
					end do;
					if found then
						Known := Known union f[2];
						Solution := [op(Solution), subSolution];
						break;
					end if;
				end if;
			end do;
			if not(found) then # không tìm thấy lời giải
				return Solution, message;
			end if;
		end do;
		return Solution, message;
	end proc;
	
	printLatex := proc(str, newline)
        local a, b, c;
        
        printf("$");
        latex(str);
        
        printf("$");
        if newline then printf("\n\n"); end if;
    end proc;
	
	# In ra đề bài nhận được
	printRequest := proc(hypothesis, vals)
		local function, v, req, p;
		function := "null";
		for v in vals do
			if lhs(v) = fx then
				function := rhs(v);
				break;
			end if;
		end do;
		
		printf("\\textbf{Đề bài:} Viết phương trình tiếp tuyến với đồ thị (C): y = %a", function );
		req := ""; # phần sau của đề bài
		for v in vals do
			if lhs(v) = M then
				p := rhs(v);
				req := MapleTA:-Builtin:-strcat("tại điểm M(", p[1], "; ", p[2], ")");
				break;
			elif lhs(v) = N then
				p := rhs(v);
				req := MapleTA:-Builtin:-strcat("đi qua điểm N(", p[1], "; ", p[2], ")");
				break;
			elif lhs(v) = x0 then
				req := MapleTA:-Builtin:-strcat("tại điểm có hoành độ $x_0$ = ", rhs(v));
				break;
			elif lhs(v) = k then
				req := MapleTA:-Builtin:-strcat("có hệ số góc k = ", rhs(v));
				break;
			elif lhs(v) = d then
				req := MapleTA:-Builtin:-strcat("song song với đường thẳng d: ", rhs(v));
				break;
			elif lhs(v) = d1 then
				req := MapleTA:-Builtin:-strcat("vuông góc với đường thẳng $d_1$: ", rhs(v));
				break;
			end if;
		end do;
		
		printf(req);
		printf("\n\\textbf{Lời giải:}\n\n");
	end proc;
	
	# Hiển thị lời giải
	showSolution := proc(hypothesis, Values)
		local Solution, step, i, message;
		printRequest(hypothesis, Values);
		Solution, message := findSolution(hypothesis, Values);
		if Solution = [] then
			printf("Không tìm thấy lời giải");
		else
			printf("Gọi tiếp tuyến cần tìm là delta\n\n");
			for step from 1 to nops(Solution) do
				printf("\\textbf{Bước %d:} ", step);
				printf(Solution[step][1]);
				printf("\n\n");
				for i from 2 to nops(Solution[step]) do
					printLatex(Solution[step][i], true);
				end do;
			end do;
		end if;
		
		if message <> "" then
			print(message);
		end if;
	end proc;

end module: