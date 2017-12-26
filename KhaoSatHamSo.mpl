KhaoSatHamSo := module()
    description "Module giải toán khảo sát hàm số đơn giản";
    option package;
   
    local   getSolutions,
            getUndefinedSet,
            getSequence,
            comparer,
            removeUndefinedSolutions,
            isPolynomial,
            isRationalFraction,
            printPoints,
            printLatex;

    export  getDomain, # Tìm tập xác định
            getExtremePoint, # Tìm cực trị
            getMaximumPoints, # Tìm các điểm cực đại
            getMinimumPoints, # Tìm các điểm cực tiểu
            getInflectionPoints, # Tìm các điểm uốn
            getVerticalAsymptotes, # Tìm tiệm cận đứng
            getObliqueAsymtotes, # Tìm tiệm cận xiên
            getHorizontalAsymtotes, # Tìm tiệm cận ngang
            getIncreaseIntervals, # Tìm khoảng đồng biến
            getDecreaseIntervals, # Tìm khoảng nghịch biến
            showSolution, # Hiển thị lời giải
            createPlot, # Tạo đồ thị
            bangBienThien;

    isPolynomial := proc(func)
        if type(func, polynom(anything, x)) then
            return true;
        end if;

        return false;
    end proc;

    isRationalFraction := proc(func)
        if isPolynomial(func) then
           return false;
        end if;

        if isPolynomial(denom(func)) and 
            isPolynomial(numer(func)) then
            return true;
        end if;

        return false;
    end proc;

    # Giải phương trình func = 0
    getSolutions := proc(func) 
        local solutions, sol;

        solutions := {solve(func = 0, x)};
        
        # Loại các nghiệm phức
        for sol in solutions do
            if type(sol, complex) = true and Im(sol) <> 0 then
                solutions := solutions minus {sol};
            end if;
        end do;

        return solutions;
    end proc;

    # Lấy các giá trị mà tại đó hàm không xác định
    getUndefinedSet := proc(func) 
        local undefinedSet, elem;

        if isPolynomial(func) then
            return {};
        end if;

        if isRationalFraction(func) then
            denom(func);
            undefinedSet := getSolutions(%);
        end if;

        return undefinedSet;
    end proc;

    # Loại bỏ các nghiệm không thuộc tập xác định
    removeUndefinedSolutions := proc(solutions, undefinedSet)
        local sol, newSolutions;
        
        newSolutions := solutions;
        for sol in newSolutions do
            if sol in undefinedSet then
                newSolutions := newSolutions minus {sol};
            end if;
        end do;

        return newSolutions;
    end proc;
    
    # In ra tập các điểm
    printPoints := proc(points)
        local temp, i;
        
        i := 0;
        for temp in points do
            i := i + 1;
            if i > 1 then printf(", ") end if;
            printf("(");
            printLatex(temp[1], false);
            printf("; ");
            printLatex(temp[2], false);
            printf(")");
        end do;
        printf("\n\n");
    end proc;
    
    printLatex := proc(str, newline)
        local a, b, c;
        
        printf("$");
        if type(str, fraction) then
            if str < 0 then printf("-"); end if;
            c := abs(str);
            a := numer(c);
            b := denom(c);
            printf("\\dfrac{%d}{%d}", a, b);
        elif str = infinity then
            printf("+\\infty");
        else
            latex(str);
        end if;
        
        printf("$");
        if newline then printf("\n\n"); end if;
    end proc;
    
    # Tìm tập xác định
    getDomain := proc(func)
        local   elem,
                domain,
                undefinedSet; 

        undefinedSet := getUndefinedSet(func);
        if undefinedSet = {} then
            return {};
        end if;

        domain := {};
        for elem in undefinedSet do
            domain := domain union {elem};
        end do;

        return domain;
    end proc;
    
    # Tìm cực trị
    getExtremePoint := proc(function)
        local diffLevel1, undefinedSet, solutions;
        
        undefinedSet := getUndefinedSet(func);
        diffLevel1 := diff(function, x);
        solutions := getSolutions(diffLevel1);
        solutions := removeUndefinedSolutions(solutions, undefinedSet);
        
        return solutions;
        
    end proc;

    # Tìm điểm cực đại
    getMaximumPoints := proc(func)
        local   diffLevel1, diffLevel2,
                solutions, result,
                sol, undefinedSet;
        
        undefinedSet := getUndefinedSet(func);
        diffLevel1 := diff(func, x);
        diffLevel2 := diff(diffLevel1, x);
        solutions := getSolutions(diffLevel1);
        solutions := removeUndefinedSolutions(solutions, undefinedSet);

        result := {};
        for sol in solutions do
            if evalf(eval(diffLevel2, x = sol)) < 0 then
                simplify(eval(func, x = sol));
                result := result union {[sol, %]};
            end if;
        end do;

        return result;
    end proc;

    # Tìm điểm cực tiểu 
    getMinimumPoints := proc(func)
        local   diffLevel1, diffLevel2,
                solutions, result,
                sol, undefinedSet;
        
        undefinedSet := getUndefinedSet(func);
        diffLevel1 := diff(func, x);
        diffLevel2 := diff(diffLevel1, x);
        solutions := getSolutions(diffLevel1);
        solutions := removeUndefinedSolutions(solutions, undefinedSet);
        
        result := {};
        for sol in solutions do
            if evalf(eval(diffLevel2, x = sol)) > 0 then
                simplify(eval(func, x = sol));
                result := result union {[sol, %]};
            end if;
        end do;

        return result;
    end proc;

    # Tìm điểm uốn
    getInflectionPoints := proc(func)
        local   solutions, 
                diffLevel2, 
                undefinedSet,
                result,
                sol;

        diffLevel2 := diff(func, x$2);
        undefinedSet := getUndefinedSet(func);
        solutions := getSolutions(diffLevel2);
        solutions := removeUndefinedSolutions(solutions, undefinedSet);

        result := {};
        for sol in solutions do
            simplify(eval(func, x = sol));
            result := result union {[sol, %]};
        end do;

        return result;
    end proc;

    # Tìm các tiệm cận đứng 
    getVerticalAsymptotes := proc(func)
        local expr, asymtotes, s, i;

        asymtotes := {};
        expr := denom(simplify(func));
        s := getSolutions(expr);
        for i in s do
            asymtotes := asymtotes union {'x' = i};
        end do;

        return asymtotes;
    end proc;

    # Tìm tiệm cận xiên
    getObliqueAsymtotes := proc(func)
        local a, b, asymtotes;

        asymtotes := {};

        a := limit(func / x, x = infinity);
        if evalb(a > -infinity) and evalb(a < infinity) and evalb(a <> 0) then
            b := limit(func - a * x, x = infinity);
            if evalb(b > -infinity) and evalb(b < infinity) then
                asymtotes := asymtotes union {a * x + b};
            end if;
        end if;

        a := limit(func / x, x = -infinity);
        if evalb(a > -infinity) and evalb(a < infinity) and evalb(a <> 0) then
            b := limit(func - a * x, x = -infinity);
            if evalb(b > -infinity) and evalb(b < infinity) then
                asymtotes := asymtotes union {a * x + b};
            end if;
        end if;

        return asymtotes;
    end proc;

    getHorizontalAsymtotes := proc(func) 
        local result, c1, c2;
        
        result := {};
        c1 := limit(func, x = infinity);
        c2 := limit(func, x = -infinity);

        if evalb(c1 > -infinity) and evalb(c1 < infinity) then
            result := result union {'y' = c1};
        end if;

        if evalb(c2 > -infinity) and evalb(c2 < infinity) then
            result := result union {'y' = c2};
        end if;
        
        return result;
    end proc;

    comparer := proc(a, b)
        return evalb(evalf(a) < evalf(b));
    end proc;

    getSequence := proc(func)
        local   s, 
                s1, s2, 
                elem, i;

        s := {};
        s1 := getMaximumPoints(func) union getMinimumPoints(func);
        
        for elem in s1 do
            s := s union {op(1, elem)};
        end do;

        s2 := getUndefinedSet(func);
        s := s union s2 union {-infinity, infinity};
        s := [op(s)];
        s := sort(s, `comparer`);

        return s;
    end proc;

    # Tìm khoảng đồng biến
    getIncreaseIntervals := proc(func)
        local   s, i, intervals, 
                x1, x2, y1, y2;

        intervals := [];
        s := getSequence(func);

        for i from 1 to nops(s) - 1 do
            x1 := s[i];
            x2 := s[i + 1];
            y1 := limit(func, x = x1, right);
            y2 := limit(func, x = x2, left);

            if evalb(evalf(y1) < evalf(y2)) then
                intervals := [op(intervals), [x1, x2]];
            end if;
        end do;

        return intervals;
    end proc;

    # Tìm khoảng nghịch biến
    getDecreaseIntervals := proc(func)
        local   s, i, intervals, 
                x1, x2, y1, y2;

        intervals := [];
        s := getSequence(func);

        for i from 1 to nops(s) - 1 do
            x1 := s[i];
            x2 := s[i + 1];
            y1 := limit(func, x = x1, right);
            y2 := limit(func, x = x2, left);

            if evalb(evalf(y1) > evalf(y2)) then
                intervals := [op(intervals), [x1, x2]];
            end if;
        end do;

        return intervals;
    end proc;

    # Hiển thị lời giải
    showSolution := proc(function)
        local   domain, temp, str,
                increase, decrease, 
                minimums, maximums,
                inflections, extremePoints,
                horizontals, verticals, obliques,
                val;

        printf("1. Tập xác định: D = R");
        domain := getDomain(function);
        if domain <> {} then
            printf(" \\textbackslash ");
            printLatex(domain, true);
        else
            printf("\n\n");
        end if;
        
        printf("2. Sự biến thiên:\n\n");
        printf("a) Giới hạn tại vô cực");
        if domain <> {} then
            printf(", giới hạn vô cực và tiệm cận");
        end if;
        printf("\n\n");
        
        printLatex('limit(y, x = -infinity)', false);
        printf(" = ");
        val := limit(y, x = -infinity);
        printLatex(val , true);
        
        printLatex('limit(y, x = infinity)', false);
        printf(" = ");
        val := limit(y, x = infinity);
        printLatex(val, true);
        
        for temp in domain do
            str := cat("limit(y,x=", temp ,",left);");
            printLatex(parse(str), false);
            printf(" = ");
            printLatex(limit(y, x = temp, left), true);
            
            str := cat("limit(y,x=", temp ,",right);");
            printLatex(parse(str), false);
            printf(" = ");
            printLatex(limit(y, x = temp, right), true);
        end do;
   
        horizontals := getHorizontalAsymtotes(function);
        verticals := getVerticalAsymptotes(function);
        obliques := getObliqueAsymtotes(function);
        
        if (verticals <> {}) then
            printf("Tiệm cận đứng: ");
            printLatex(op(verticals), true);
        end if;
        
        if (horizontals <> {}) then
            printf("Tiệm cận ngang: ");
            printLatex(op(horizontals), true);
        end if;

        if (obliques <> {}) then
            for temp in obliques do
                str := cat("limit(y -(", temp, "), x=-infinity);");
                printLatex(parse(str), false);
                printf(" = ");
                printLatex(limit(y-temp,x=-infinity), true);
                
                str := cat("limit(y -(", temp, "), x=infinity);");
                printLatex(parse(str), false);
                printf(" = ");
                printLatex(limit(y-temp,x=infinity), true);
                printf("Nên đường thẳng y = ");
                printLatex(temp, false);
                printf(" là tiệm cận xiên của đồ thị hàm số đã cho.\n\n");
            end do;
        end if;
        
        printf("b) Bảng biến thiên\n\n");
        printf("Ta có: y' = ");
        val := diff(y, x);
        printLatex(val, true);
        
        extremePoints := getExtremePoint(function);
        if extremePoints <> {} then
            printf("y' = 0 $\\left<=\\right>$ x = ");
            printLatex(extremePoints, true);
        end if;

        increase := getIncreaseIntervals(function);
        if (increase <> []) then
            printf("Hàm số đồng biến trên các khoảng: ");
            printPoints(increase);
        end if;
        
        decrease := getDecreaseIntervals(function);
        if (decrease <> []) then
            printf("Hàm số nghịch biến trên các khoảng: ");
            printPoints(decrease);
        end if;

        minimums := getMinimumPoints(function);
        maximums := getMaximumPoints(function);
        inflections := getInflectionPoints(function);

        if (minimums <> {}) then
            printf("Hàm số đạt cực tiểu tại: ");
            printPoints(minimums);
        end if;
        
        if (maximums <> {}) then
            printf("Hàm số đạt cực đại tại: ");
            printPoints(maximums);
        end if;
        
        printf("\n\n");
        bangBienThien(function);
        
        printf("\n\n3. Đồ thị\n\n");
        
        if (inflections <> {}) then
            printf("Các điểm uốn: ");
            printLatex(inflections, true);
        end if;

        createPlot(function);
        printf("\\includegraphics[scale=.5]{plot.jpg}\n\n");

    end proc;
    
    createPlot := proc(function)
        local verticals, horizontals, obliques, inflection, extremePoints,
        str, temp, temp1, minValue, maxValue, center, sol, soCucTri, undefinedSet, graph;
        
        verticals := getVerticalAsymptotes(function);
        horizontals := getHorizontalAsymtotes(function);
        obliques := getObliqueAsymtotes(function);
        inflection := getInflectionPoints(function);
        extremePoints := getExtremePoint(function);
        
        # ve ham, tiem can ngang, tiem can xien
        str := cat("", function);
        if horizontals <> {} then
            for temp in horizontals do
                str := cat(str, ",", rhs(temp));
            end do;
        end if;
        
        if obliques <> {} then
            str := cat(str, ",", op(obliques));
        end if;
        
        temp := parse(str);
        temp := [temp];
        
        # tim khoang gioi han
        minValue := 0;
        maxValue := 0;
        sol := getSolutions(function); # giai phuong trinh
        sol := sol union extremePoints; # lay tap hop cac nghiem va cuc tri
        undefinedSet := getUndefinedSet(function);
        sol := removeUndefinedSolutions(sol, undefinedSet);
        
        if sol <> {} then
            minValue := min(sol);
            maxValue := max(sol);
        end if;
        
        # Tim tam/truc doi xung
        center := {};
        sort(extremePoints, `comparer`);
        soCucTri := numelems(extremePoints);
        if isPolynomial(function) then
            if (degree(function) mod 2) = 0 then
                # diem cuc tri chinh giua la truc doi xung
                center := extremePoints[round(soCucTri / 2)];
            end if;
        elif verticals <> {} then
            # giao diem tiem can dung va ngang(xien) la tam doi xung
            center := rhs(verticals[1]);
        elif inflection <> {} then
            center := inflection[1][1]; 
        end if;
        
        if center <> {} then
            # lay khoang tu tam den nghiem xa nhat
            temp1 := max(abs(center - minValue), abs(center - maxValue));
            minValue := center - temp1;
            maxValue := center + temp1;
        end if;
        
        graph := plot(temp, x=minValue-2..maxValue+2);
        Export("plot.jpg", graph);
    end proc;

    bangBienThien := proc(function)
        local points, numPoints, dongBien, nghichBien, dau, i, undefinedSet, a, b;
        
        printf("\\begin{tikzpicture}\n");
        printf("\\tkzTabInit[nocadre=false,lgt=1,espcl=2]\n");
        printf("{$x$ /1,$y'$ /1,$y$ /2}");
        
        undefinedSet := getUndefinedSet(function);
        points := getSequence(function);
        numPoints := numelems(points);
        
        dongBien := getIncreaseIntervals(function);
        nghichBien := getDecreaseIntervals(function);
        
        printf("{");
        for i from 1 to numPoints do
            if i > 1 then printf(",") end if;
            printLatex(points[i], false);
        end do;
        printf("}\n");
        
        printf("\\tkzTabLine{");
        dau := [];
        for i from 1 to numPoints - 1 do
            if ([points[i], points[i + 1]] in dongBien) then
                dau := [op(dau), "+"];
            else
                dau := [op(dau), "-"];
            end if;
            
            if i > 1 then
                if points[i] in undefinedSet then
                    printf("d");
                else
                    printf("$0$");
                end if;
            end if;
            printf(",%s,", dau[i]);
        end do;
        printf("}\n");
        
        printf("\\tkzTabVar{");
        for i from 1 to numPoints do
            if i > 1 then printf(", "); end if;
            
            if points[i] in undefinedSet then
                if dau[i - 1] = "+" then printf("+"); else printf("-"); end if;
                printf("D");
                if dau[i] = "+" then printf("-"); else printf("+"); end if;
                a := limit(function, x = points[i], left);
                b := limit(function, x = points[i], right);
                printf("/ ");
                printLatex(a, false);
                printf(" / ");
                printLatex(b, false);
            else
                if i = numPoints then
                    if dau[i - 1] = "+" then printf("+"); else printf("-"); end if;
                else
                    if dau[i] = "+" then printf("-"); else printf("+"); end if;
                end if;
                
                printf("/ ");
                a := limit(function, x = points[i]);
                printLatex(a, false);
                printf(" /");
            end if;
        end do;
        printf("}\n");
        
        printf("\\end{tikzpicture}");
        
    end proc;

end module: