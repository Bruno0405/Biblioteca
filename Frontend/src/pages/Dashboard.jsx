import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
faBookOpen,
faBookmark,
faHouse,
faMoneyCheckDollar,
faSearch,
faXmark,
faChevronLeft,
faChevronRight,
faCircleCheck,
faCircleXmark,
} from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";
import { AuthContext } from "../context/AuthContext";
import apiClient from "../services/apiClient";

function today() {
return new Date().toISOString().slice(0, 10);
}

const COVER_PALETTES = [
	{ bg: "linear-gradient(145deg,#3b5bdb 0%,#6580e8 100%)", color: "#eef2ff" },
	{ bg: "linear-gradient(145deg,#0f766e 0%,#2dd4bf 100%)", color: "#ecfdf5" },
	{ bg: "linear-gradient(145deg,#7c3aed 0%,#a78bfa 100%)", color: "#f5f3ff" },
	{ bg: "linear-gradient(145deg,#b45309 0%,#f59e0b 100%)", color: "#fffbeb" },
	{ bg: "linear-gradient(145deg,#be185d 0%,#f472b6 100%)", color: "#fdf2f8" },
	{ bg: "linear-gradient(145deg,#0369a1 0%,#38bdf8 100%)", color: "#f0f9ff" },
	{ bg: "linear-gradient(145deg,#166534 0%,#4ade80 100%)", color: "#f0fdf4" },
	{ bg: "linear-gradient(145deg,#9f1239 0%,#fb7185 100%)", color: "#fff1f2" },
];

function coverPalette(id) {
return COVER_PALETTES[Number(id || 0) % COVER_PALETTES.length];
}

const NAV_ITEMS = [
{ key: "inicio", label: "Início", icon: faHouse },
{ key: "catalogo", label: "Acervo", icon: faSearch },
{ key: "reservas", label: "Minhas Reservas", icon: faBookmark },
{ key: "multas", label: "Minhas Multas", icon: faMoneyCheckDollar },
];

function LivroCard({ livro, estoquePorLivro, fotoPorLivro, autorPorLivro, generoPorLivro, onVerLivro }) {
const disponibilidade = estoquePorLivro.get(Number(livro.idLivro)) ?? 0;
const cover = fotoPorLivro.get(Number(livro.idLivro));
const palette = coverPalette(livro.idLivro);
const autores = autorPorLivro.get(Number(livro.idLivro));
const genero = generoPorLivro.get(Number(livro.idLivro));

return (
<article
className="carousel-book-card"
role="button"
tabIndex={0}
onClick={() => onVerLivro(livro)}
onKeyDown={(e) => e.key === "Enter" && onVerLivro(livro)}
title={"Ver detalhes de " + livro.nomeLivro}
>
<div className="cover-shell" style={cover ? {} : { background: palette.bg }}>
{cover
? <img src={cover} alt={"Capa de " + livro.nomeLivro} />
: (
<div className="cover-fallback-art" style={{ color: palette.color }}>
<FontAwesomeIcon icon={faBookOpen} size="2x" />
<span>{livro.nomeLivro ? livro.nomeLivro.slice(0, 22) : ""}</span>
{genero && <small>{genero}</small>}
</div>
)}
</div>
<div className="book-card-body">
<h4>{livro.nomeLivro}</h4>
{autores && <p className="book-author">{autores}</p>}
<p className="book-publisher">{livro.editora || "Editora não informada"}</p>
<div className="book-meta-row">
<span className={disponibilidade > 0 ? "status-ok" : "status-off"}>
{disponibilidade > 0
? <><FontAwesomeIcon icon={faCircleCheck} /> {disponibilidade} disponível</>
: <><FontAwesomeIcon icon={faCircleXmark} /> Indisponível</>}
</span>
</div>
</div>
</article>
);
}

function LivroModal({ livro, estoquePorLivro, fotoPorLivro, autorPorLivro, generoPorLivro, onClose, onReservar, reservaLoading, reservaError }) {
const [dataReserva, setDataReserva] = useState(today());
const [dataDevolucao, setDataDevolucao] = useState("");
const [codigoReserva] = useState(() => "RSV-" + Math.random().toString(36).slice(2, 8).toUpperCase());

const disponibilidade = estoquePorLivro.get(Number(livro.idLivro)) ?? 0;
const cover = fotoPorLivro.get(Number(livro.idLivro));
const palette = coverPalette(livro.idLivro);
const autores = autorPorLivro.get(Number(livro.idLivro));
const genero = generoPorLivro.get(Number(livro.idLivro));

function handleSubmit(e) {
e.preventDefault();
onReservar({
idLivro: livro.idLivro,
dataReserva,
dataPrevistaDevolucao: dataDevolucao || undefined,
codigoReserva: codigoReserva || undefined,
});
}

useEffect(() => {
function onKey(e) { if (e.key === "Escape") onClose(); }
document.addEventListener("keydown", onKey);
return () => document.removeEventListener("keydown", onKey);
}, [onClose]);

return (
<div className="modal-overlay" role="dialog" aria-modal="true" onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}>
<div className="modal-card">
<button className="modal-close" onClick={onClose} aria-label="Fechar">
<FontAwesomeIcon icon={faXmark} />
</button>

<div className="modal-body">
<div className="modal-cover-col">
<div className="modal-cover-shell" style={cover ? {} : { background: palette.bg }}>
{cover
? <img src={cover} alt={"Capa de " + livro.nomeLivro} />
: (
<div className="cover-fallback-art" style={{ color: palette.color }}>
<FontAwesomeIcon icon={faBookOpen} size="3x" />
<span>{livro.nomeLivro ? livro.nomeLivro.slice(0, 28) : ""}</span>
{genero && <small>{genero}</small>}
</div>
)}
</div>
<div className={"modal-disponibilidade " + (disponibilidade > 0 ? "ok" : "off")}>
<FontAwesomeIcon icon={disponibilidade > 0 ? faCircleCheck : faCircleXmark} />
{disponibilidade > 0 ? " " + disponibilidade + " exemplar(es) disponível(is)" : " Sem exemplares disponíveis"}
</div>
</div>

<div className="modal-info-col">
<p className="kicker">{genero || "Livro"}</p>
<h2>{livro.nomeLivro}</h2>
{autores && <p className="modal-autores">{autores}</p>}
<div className="modal-meta">
{livro.editora && <span><strong>Editora:</strong> {livro.editora}</span>}
{livro.ano && <span><strong>Ano:</strong> {livro.ano}</span>}
{livro.isnb && <span><strong>ISBN:</strong> {livro.isnb}</span>}
{livro.localizacaoFisica && <span><strong>Localização:</strong> {livro.localizacaoFisica}</span>}
</div>

{livro.sinopse && (
<div className="modal-sinopse">
<h4>Sinopse</h4>
<p>{livro.sinopse}</p>
</div>
)}

{disponibilidade > 0 && (
<form className="modal-reserva-form" onSubmit={handleSubmit}>
<h4>Reservar este livro</h4>
{reservaError && <p className="form-error">{String(reservaError)}</p>}
<div className="form-grid cols-2">
<label>
Data da reserva
<input type="date" value={dataReserva} onChange={(e) => setDataReserva(e.target.value)} required />
</label>
<label>
Previsão de devolução
<input type="date" value={dataDevolucao} onChange={(e) => setDataDevolucao(e.target.value)} />
</label>
<label>
Código da reserva
<input type="text" value={codigoReserva} readOnly style={{ background: "#f1f5f9", color: "var(--text-soft)", cursor: "default" }} />
</label>
</div>
<div className="button-row" style={{ marginTop: 14 }}>
<button type="submit" disabled={reservaLoading}>
{reservaLoading ? "Reservando..." : "Confirmar Reserva"}
</button>
</div>
</form>
)}
</div>
</div>
</div>
</div>
);
}

function Carousel({ title, livros, estoquePorLivro, fotoPorLivro, autorPorLivro, generoPorLivro, onVerLivro }) {
const trackRef = useRef(null);
const intervalRef = useRef(null);

function scroll(dir) {
if (!trackRef.current) return;
trackRef.current.scrollBy({ left: dir === "left" ? -260 : 260, behavior: "smooth" });
}

function startAutoplay() {
intervalRef.current = setInterval(() => scroll("right"), 3500);
}

function stopAutoplay() {
clearInterval(intervalRef.current);
}

useEffect(() => {
startAutoplay();
return () => stopAutoplay();
// eslint-disable-next-line react-hooks/exhaustive-deps
}, []);

if (livros.length === 0) return null;

return (
<section className="panel carousel-panel">
<div className="carousel-header-row">
<h3>{title}</h3>
<div className="carousel-actions">
<button type="button" className="carousel-btn" onClick={() => scroll("left")} aria-label="Anterior">
<FontAwesomeIcon icon={faChevronLeft} />
</button>
<button type="button" className="carousel-btn" onClick={() => scroll("right")} aria-label="Próximo">
<FontAwesomeIcon icon={faChevronRight} />
</button>
</div>
</div>
<div
className="carousel-track"
ref={trackRef}
onMouseEnter={stopAutoplay}
onMouseLeave={startAutoplay}
>
{livros.map((livro) => (
<LivroCard
key={livro.idLivro}
livro={livro}
estoquePorLivro={estoquePorLivro}
fotoPorLivro={fotoPorLivro}
autorPorLivro={autorPorLivro}
generoPorLivro={generoPorLivro}
onVerLivro={onVerLivro}
/>
))}
</div>
</section>
);
}

function Dashboard() {
const navigate = useNavigate();
const { user, logout } = useContext(AuthContext);

const [livros, setLivros] = useState([]);
const [reservas, setReservas] = useState([]);
const [estoques, setEstoques] = useState([]);
const [fotos, setFotos] = useState([]);
const [autores, setAutores] = useState([]);
const [livroAutores, setLivroAutores] = useState([]);
const [generos, setGeneros] = useState([]);
const [livroGeneros, setLivroGeneros] = useState([]);
const [multas, setMultas] = useState([]);

const [loading, setLoading] = useState(false);
const [error, setError] = useState("");
const [busca, setBusca] = useState("");
const [secao, setSecao] = useState("inicio");
const [livroAberto, setLivroAberto] = useState(null);
const [reservaLoading, setReservaLoading] = useState(false);
const [reservaError, setReservaError] = useState("");
const [reservaSucesso, setReservaSucesso] = useState("");

const secaoRef = useRef(null);

const carregarDados = useCallback(async () => {
if (!user?.id) return;
setLoading(true);
setError("");
try {
const [
livrosRes, reservasRes, estoquesRes, fotosRes,
autoresRes, livroAutoresRes, generosRes, livroGenerosRes
] = await Promise.all([
apiClient.get("/livros"),
apiClient.get("/reservas", { params: { idCliente: user.id } }),
apiClient.get("/estoque"),
apiClient.get("/fotos"),
apiClient.get("/autores"),
apiClient.get("/livro-autor"),
apiClient.get("/generos"),
apiClient.get("/livro-genero"),
]);

setLivros(livrosRes.data || []);
setReservas(reservasRes.data || []);
setEstoques(estoquesRes.data || []);
setFotos(fotosRes.data || []);
setAutores(autoresRes.data || []);
setLivroAutores(livroAutoresRes.data || []);
setGeneros(generosRes.data || []);
setLivroGeneros(livroGenerosRes.data || []);

try {
const multasRes = await apiClient.get("/multas", { params: { idCliente: user.id } });
setMultas(multasRes.data || []);
} catch {
setMultas([]);
}
} catch (err) {
setError(err.response?.data || "Erro ao carregar dados da biblioteca.");
} finally {
setLoading(false);
}
}, [user?.id]);

useEffect(() => { carregarDados(); }, [carregarDados]);

function irParaSecao(key) {
setSecao(key);
setTimeout(() => {
secaoRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
}, 50);
}

const estoquePorLivro = useMemo(() => {
const map = new Map();
estoques.forEach((e) => {
const disp = Math.max(
Number(e.quantidadeTotal || 0)
- Number(e.quantidadeReservada || 0)
- Number(e.quantidadeEmprestada || 0)
- Number(e.quantidadeDanificada || 0),
0
);
map.set(Number(e.idLivro), disp);
});
return map;
}, [estoques]);

const fotoPorLivro = useMemo(() => {
const map = new Map();
fotos.forEach((f) => {
if (!map.has(Number(f.idLivro)) && f.foto) map.set(Number(f.idLivro), f.foto);
});
return map;
}, [fotos]);

const autorPorLivro = useMemo(() => {
const autorNome = new Map(autores.map((a) => [Number(a.idAutor), a.nomeAutor]));
const map = new Map();
livroAutores.forEach((la) => {
const nome = autorNome.get(Number(la.idAutor));
if (!nome) return;
const prev = map.get(Number(la.idLivro));
map.set(Number(la.idLivro), prev ? prev + ", " + nome : nome);
});
return map;
}, [autores, livroAutores]);

const generoPorLivro = useMemo(() => {
const generoNome = new Map(generos.map((g) => [Number(g.idGenero), g.nomeGenero]));
const map = new Map();
livroGeneros.forEach((lg) => {
const nome = generoNome.get(Number(lg.idGenero));
if (!nome) return;
if (!map.has(Number(lg.idLivro))) map.set(Number(lg.idLivro), nome);
});
return map;
}, [generos, livroGeneros]);

const livrosDisponiveis = useMemo(
() => livros.filter((l) => (estoquePorLivro.get(Number(l.idLivro)) || 0) > 0),
[livros, estoquePorLivro]
);

const ultimosAdicionados = useMemo(
() => [...livros].sort((a, b) => Number(b.idLivro) - Number(a.idLivro)).slice(0, 20),
[livros]
);

const destaques = useMemo(() => {
const comSinopse = livros.filter((l) => l.sinopse && l.sinopse.trim());
return (comSinopse.length > 0 ? comSinopse : [...livros]).slice(0, 20);
}, [livros]);

const nomeLivroPorReserva = useMemo(() => {
const livroNome = new Map(livros.map((l) => [Number(l.idLivro), l.nomeLivro]));
const map = new Map();
reservas.forEach((r) => {
const nome = livroNome.get(Number(r.idLivro));
if (nome) map.set(Number(r.idReserva), nome);
});
return map;
}, [livros, reservas]);

const livrosFiltrados = useMemo(() => {
const t = busca.trim().toLowerCase();
if (!t) return livros;
return livros.filter((l) =>
[l.nomeLivro, l.editora, l.isnb, l.localizacaoFisica, autorPorLivro.get(Number(l.idLivro)), generoPorLivro.get(Number(l.idLivro))]
.filter(Boolean)
.some((v) => String(v).toLowerCase().includes(t))
);
}, [livros, busca, autorPorLivro, generoPorLivro]);

async function reservarLivro({ idLivro, dataReserva, dataPrevistaDevolucao, codigoReserva }) {
setReservaLoading(true);
setReservaError("");
setReservaSucesso("");
try {
await apiClient.post("/reservas", {
idCliente: Number(user.id),
idLivro: Number(idLivro),
dataReserva,
dataPrevistaDevolucao,
codigoReserva,
statusReserva: "reservado",
});
setReservaSucesso("Reserva realizada com sucesso!");
setLivroAberto(null);
await carregarDados();
irParaSecao("reservas");
} catch (err) {
setReservaError(err.response?.data || "Não foi possível reservar.");
} finally {
setReservaLoading(false);
}
}

function handleLogout() {
logout();
navigate("/");
}

const cardProps = { estoquePorLivro, fotoPorLivro, autorPorLivro, generoPorLivro, onVerLivro: setLivroAberto };

return (
<Layout
title="Biblioteca Virtual"
subtitle={"Bem-vindo, " + (user?.name || "leitor") + "!"}
userName={user?.name}
onLogout={handleLogout}
>
<div className="dashboard-grid">
<aside className="sidebar-nav">
<div className="sidebar-brand">
<FontAwesomeIcon icon={faBookOpen} className="sidebar-brand-icon" />
<span>Minha Biblioteca</span>
</div>
<nav>
{NAV_ITEMS.map((item) => (
<button
key={item.key}
type="button"
className={"sidebar-nav-item" + (secao === item.key ? " active" : "")}
onClick={() => irParaSecao(item.key)}
>
<FontAwesomeIcon icon={item.icon} />
<span>{item.label}</span>
</button>
))}
</nav>
<div className="sidebar-footer">
<p className="muted">{user?.email}</p>
</div>
</aside>

<main className="main-content" ref={secaoRef}>
{error && <p className="form-error">{String(error)}</p>}
{reservaSucesso && <p className="form-success">{reservaSucesso}</p>}

{secao === "inicio" && (
<>
<Carousel title="📗 Disponíveis Agora" livros={livrosDisponiveis} {...cardProps} />
<Carousel title="🆕 Últimos Adicionados" livros={ultimosAdicionados} {...cardProps} />
<Carousel title="⭐ Destaques" livros={destaques} {...cardProps} />
</>
)}

{secao === "catalogo" && (
<section className="panel">
<h3>Acervo da Biblioteca</h3>
<div className="catalog-search">
<FontAwesomeIcon icon={faSearch} className="search-icon" />
<input
type="text"
placeholder="Buscar por nome, autor, gênero, editora, ISBN..."
value={busca}
onChange={(e) => setBusca(e.target.value)}
/>
</div>
{loading
? <p>Carregando acervo...</p>
: (
<div className="catalog-grid">
{livrosFiltrados.length === 0
? <p className="muted">Nenhum livro encontrado.</p>
: livrosFiltrados.map((livro) => (
<LivroCard key={livro.idLivro} livro={livro} {...cardProps} />
))}
</div>
)}
</section>
)}

{secao === "reservas" && (
<section className="panel">
<h3>Minhas Reservas</h3>
{loading
? <p>Carregando reservas...</p>
: reservas.length === 0
? (
<p className="muted">
Você ainda não possui reservas.{" "}
<button type="button" className="link-btn" onClick={() => irParaSecao("catalogo")}>
Explorar o acervo →
</button>
</p>
)
: (
<div className="table-wrap">
<table>
<thead>
<tr>
<th>Código</th>
<th>Livro</th>
<th>Data Reserva</th>
<th>Previsão Devolução</th>
<th>Status</th>
</tr>
</thead>
<tbody>
{reservas.map((r) => {
const livro = livros.find((l) => Number(l.idLivro) === Number(r.idLivro));
return (
<tr key={r.idReserva}>
<td>{r.codigoReserva || "-"}</td>
<td>
{livro
? <button type="button" className="link-btn" onClick={() => setLivroAberto(livro)}>{livro.nomeLivro}</button>
: r.idLivro}
</td>
<td>{r.dataReserva || "-"}</td>
<td>{r.dataPrevistaDevolucao || "-"}</td>
<td>
<span className={"status-badge " + (r.statusReserva === "reservado" ? "ok" : "")}>
{r.statusReserva || "-"}
</span>
</td>
</tr>
);
})}
</tbody>
</table>
</div>
)}
</section>
)}

{secao === "multas" && (
<section className="panel">
<h3>Minhas Multas</h3>
{loading
? <p>Carregando multas...</p>
: multas.length === 0
? <p className="muted">Você não possui multas em aberto.</p>
: (
<div className="table-wrap">
<table>
<thead>
<tr>
<th>Livro</th>
<th>Valor</th>
<th>Data</th>
<th>Status</th>
</tr>
</thead>
<tbody>
{multas.map((m) => (
<tr key={m.idMulta}>
<td>{nomeLivroPorReserva.get(Number(m.idReserva)) || "-"}</td>
<td>{m.valorMulta != null ? Number(m.valorMulta).toLocaleString("pt-BR", { style: "currency", currency: "BRL" }) : "-"}</td>
<td>{m.dataMulta || "-"}</td>
<td>
<span className={"status-badge " + (m.statusMulta === "pago" ? "ok" : "off")}>
{m.statusMulta || "-"}
</span>
</td>
</tr>
))}
</tbody>
</table>
</div>
)}
</section>
)}
</main>
</div>

{livroAberto && (
<LivroModal
livro={livroAberto}
estoquePorLivro={estoquePorLivro}
fotoPorLivro={fotoPorLivro}
autorPorLivro={autorPorLivro}
generoPorLivro={generoPorLivro}
onClose={() => { setLivroAberto(null); setReservaError(""); }}
onReservar={reservarLivro}
reservaLoading={reservaLoading}
reservaError={reservaError}
/>
)}
</Layout>
);
}

export default Dashboard;
