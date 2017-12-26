packageDir := cat(currentdir(), kernelopts(dirsep), "PhuongTrinhtiepTuyen.mla"):
march('open', packageDir):
with(PhuongTrinhTiepTuyen):
showSolution({fx, d_1}, {fx = 3*x^3 - 7*x + 2, d_1 = "2*x + 3*y=1"});