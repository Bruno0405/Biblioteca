import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
	faArrowsRotate,
	faBook,
	faBookmark,
	faBookOpen,
	faBoxesStacked,
	faCamera,
	faCheck,
	faClockRotateLeft,
	faFileLines,
	faMoneyCheckDollar,
	faPenNib,
	faPencil,
	faPlus,
	faTag,
	faTrash,
	faUserTie,
	faUsers,
	faXmark,
} from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { getResourceByKey } from "../config/resources";
import apiClient from "../services/apiClient";
import Layout from "../components/Layout";

const RESOURCE_ICONS = {
	autores: faPenNib,
	clientes: faUsers,
	estoque: faBoxesStacked,
	fotos: faCamera,
	funcionarios: faUserTie,
	generos: faTag,
	"historico-cliente": faClockRotateLeft,
	"livro-autor": faBookOpen,
	"livro-genero": faBookOpen,
	livros: faBook,
	logs: faFileLines,
	"movimentacao-estoque": faArrowsRotate,
	multas: faMoneyCheckDollar,
	reservas: faBookmark,
};

const SIDEBAR_GROUPS = [
	{ label: "Acervo", keys: ["livros", "autores", "generos", "fotos", "livro-autor", "livro-genero"] },
	{ label: "Pessoas", keys: ["clientes", "funcionarios"] },
	{ label: "Operações", keys: ["estoque", "movimentacao-estoque", "reservas", "multas"] },
	{ label: "Sistema", keys: ["logs", "historico-cliente"] },
];

// ── Painel genérico de CRUD ────────────────────────────────────────────────────
function ResourcePanel({ resource }) {
	const [items, setItems] = useState([]);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");
	const [modalOpen, setModalOpen] = useState(false);
	const [editItem, setEditItem] = useState(null);
	const [formData, setFormData] = useState({});
	const [confirmDeleteKey, setConfirmDeleteKey] = useState(null);
	const [saving, setSaving] = useState(false);
	const [lookupData, setLookupData] = useState({});

	const carregarDados = useCallback(async () => {
		setLoading(true);
		setError("");
		try {
			const res = await apiClient.get(resource.path);
			setItems(Array.isArray(res.data) ? res.data : []);
		} catch {
			setError("Erro ao carregar " + resource.label + ". Verifique se o backend está ativo.");
		} finally {
			setLoading(false);
		}
	}, [resource.path, resource.label]);

	useEffect(() => { carregarDados(); }, [carregarDados]);

	// Carrega dados de lookup (ex: lista de livros para campo idLivro)
	useEffect(() => {
		const lookupResources = {};
		resource.fields.forEach((f) => {
			if (f.lookup) lookupResources[f.lookup.resource] = true;
		});
		const keys = Object.keys(lookupResources);
		if (keys.length === 0) return;

		Promise.all(
			keys.map((key) => {
				const res = getResourceByKey(key);
				return res ? apiClient.get(res.path).then((r) => ({ key, data: r.data })) : null;
			}).filter(Boolean)
		).then((results) => {
			const map = {};
			results.forEach(({ key, data }) => { map[key] = Array.isArray(data) ? data : []; });
			setLookupData(map);
		}).catch(() => {});
	}, [resource]);

	function getItemKey(item) {
		return resource.idFields.map((f) => String(item[f])).join("-");
	}

	function openCreate() {
		setEditItem(null);
		// Pré-preenche campos autoGenerate
		const initial = {};
		resource.fields.forEach((f) => {
			if (f.autoGenerate) initial[f.name] = "RSV-" + Math.random().toString(36).slice(2, 8).toUpperCase();
		});
		setFormData(initial);
		setError("");
		setModalOpen(true);
	}

	function openEdit(item) {
		setEditItem(item);
		const data = {};
		resource.fields.filter((f) => !f.createOnly).forEach((f) => {
			data[f.name] = item[f.name] ?? "";
		});
		setFormData(data);
		setError("");
		setModalOpen(true);
	}

	function todayISO() {
		return new Date().toISOString().split("T")[0];
	}

	function nowISO() {
		const now = new Date();
		return now.toISOString().slice(0, 16); // "YYYY-MM-DDTHH:mm"
	}

	function coercePayload() {
		const out = {};
		resource.fields.forEach((field) => {
			const value = formData[field.name];
			const isEmpty = value === "" || value === undefined || value === null;
			if (isEmpty) {
				if (field.type === "date") {
					out[field.name] = todayISO();
				} else if (field.type === "datetime-local") {
					out[field.name] = nowISO();
				} else if (field.type === "number" || field.type === "decimal") {
					out[field.name] = undefined;
				} else {
					out[field.name] = undefined;
				}
			} else if (field.type === "number" || field.type === "decimal") {
				out[field.name] = Number(value);
			} else if (field.type === "boolean") {
				out[field.name] = value === true || value === "true";
			} else {
				out[field.name] = value;
			}
		});
		return out;
	}

	async function handleSubmit(e) {
		e.preventDefault();
		setSaving(true);
		setError("");
		try {
			const payload = coercePayload();
			if (editItem) {
				const id = editItem[resource.idFields[0]];
				await apiClient.put(resource.path + "/" + id, payload);
			} else {
				await apiClient.post(resource.path, payload);
			}
			setModalOpen(false);
			await carregarDados();
		} catch (e) {
			setError(e.response?.data || "Erro ao salvar. Verifique os dados.");
		} finally {
			setSaving(false);
		}
	}

	async function handleDelete(item) {
		setError("");
		try {
			const idPath = resource.idFields.map((f) => item[f]).join("/");
			await apiClient.delete(resource.path + "/" + idPath);
			setConfirmDeleteKey(null);
			await carregarDados();
		} catch (e) {
			setError(e.response?.data || "Erro ao excluir.");
		}
	}

	const tableColumns = useMemo(() => {
		const seen = new Set(resource.idFields);
		const idCols = resource.idFields.map((f) => ({ name: f, label: f }));
		const fieldCols = resource.fields
			.filter((f) => !f.createOnly && !seen.has(f.name))
			.map((f) => ({ name: f.name, label: f.label }));
		return [...idCols, ...fieldCols];
	}, [resource]);

	const formFields = useMemo(
		() => (editItem ? resource.fields.filter((f) => !f.createOnly) : resource.fields),
		[resource, editItem]
	);

	function setField(name, value) {
		setFormData((prev) => ({ ...prev, [name]: value }));
	}

	function renderInput(field) {
		const value = formData[field.name] ?? "";

		// Campo auto-gerado: exibe como leitura somente
		if (field.autoGenerate) {
			return (
				<input
					type="text"
					value={value || "(gerado automaticamente)"}
					readOnly
					style={{ background: "#f1f5f9", color: "var(--text-soft)", cursor: "not-allowed" }}
				/>
			);
		}

		// Select de opções fixas (perfil, status, tipo)
		if (field.options) {
			return (
				<select value={String(value)} onChange={(e) => setField(field.name, e.target.value)}>
					<option value="">Selecione...</option>
					{field.options.map((opt) => (
						<option key={opt.value} value={opt.value}>{opt.label}</option>
					))}
				</select>
			);
		}

		// Select de lookup (FK com nomes)
		if (field.lookup) {
			const lookupRes = getResourceByKey(field.lookup.resource);
			const idField = field.lookup.idField || (lookupRes ? lookupRes.idFields[0] : "id");
			const options = lookupData[field.lookup.resource] || [];
			return (
				<select value={String(value)} onChange={(e) => setField(field.name, e.target.value)}>
					<option value="">Selecione...</option>
					{options.map((opt) => (
						<option key={opt[idField]} value={opt[idField]}>
							{opt[field.lookup.labelField] || opt[idField]}
						</option>
					))}
				</select>
			);
		}

		if (field.type === "boolean") {
			return (
				<select value={String(value)} onChange={(e) => setField(field.name, e.target.value === "true")}>
					<option value="">Selecione...</option>
					<option value="true">Sim</option>
					<option value="false">Não</option>
				</select>
			);
		}
		const inputType =
			field.type === "number" || field.type === "decimal"
				? "number"
				: field.type === "text" || !field.type
					? "text"
					: field.type;
		if (field.type === "decimal") {
			// Formata em tempo real: digita "1250" → exibe "R$ 1.250" / "1250,50" → "R$ 1.250,50"
			const handleDecimalChange = (e) => {
				// Remove tudo que não é dígito
				const digits = e.target.value.replace(/\D/g, "");
				if (!digits) { setField(field.name, ""); return; }
				// Últimos 2 dígitos são centavos
				const cents = digits.slice(-2).padStart(2, "0");
				const reais = digits.slice(0, -2) || "0";
				const numeric = parseFloat(reais + "." + cents);
				setField(field.name, numeric);
			};
			const displayValue = value !== "" && value !== undefined
				? Number(value).toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
				: "";
			return (
				<div style={{ position: "relative", display: "flex", alignItems: "center" }}>
					<span style={{ position: "absolute", left: 13, color: "var(--text-soft)", fontSize: 13, fontWeight: 600, pointerEvents: "none" }}>R$</span>
					<input
						type="text"
						inputMode="numeric"
						value={displayValue}
						onChange={handleDecimalChange}
						placeholder="0,00"
						style={{ paddingLeft: 36 }}
					/>
				</div>
			);
		}
		return (
			<input
				type={inputType}
				step={field.type === "decimal" ? "0.01" : undefined}
				value={value}
				onChange={(e) => setField(field.name, e.target.value)}
			/>
		);
	}

	function resolveCellValue(col, rawValue) {
		const field = resource.fields.find((f) => f.name === col.name);
		if (!field) return truncate(rawValue);

		// Moeda
		if (field.type === "decimal" && rawValue !== undefined && rawValue !== null && rawValue !== "") {
			return Number(rawValue).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
		}

		// Resolve opção fixa (status, perfil, tipo)
		if (field.options) {
			const opt = field.options.find((o) => o.value === String(rawValue));
			return opt ? opt.label : truncate(rawValue);
		}

		// Resolve nome do lookup (FK)
		if (field.lookup) {
			const lookupRes = getResourceByKey(field.lookup.resource);
			const idField = field.lookup.idField || (lookupRes ? lookupRes.idFields[0] : "id");
			const options = lookupData[field.lookup.resource] || [];
			const found = options.find((o) => String(o[idField]) === String(rawValue));
			return found ? (found[field.lookup.labelField] || truncate(rawValue)) : truncate(rawValue);
		}

		return truncate(rawValue);
	}

	function truncate(value) {
		const str = String(value ?? "-");
		return str.length > 32 ? str.slice(0, 32) + "…" : str;
	}

	return (
		<section className="panel admin-resource-panel">
			<div className="admin-panel-header">
				<div>
					<h3>{resource.label}</h3>
					<p className="muted" style={{ fontSize: 12 }}>{resource.path}</p>
				</div>
				<button type="button" onClick={openCreate}>
					<FontAwesomeIcon icon={faPlus} />
					<span style={{ marginLeft: 7 }}>Adicionar</span>
				</button>
			</div>

			{error && <p className="form-error" style={{ marginBottom: 14 }}>{String(error)}</p>}

			{loading ? (
				<p className="muted">Carregando {resource.label}...</p>
			) : (
				<div className="table-wrap">
					<table>
						<thead>
							<tr>
								{tableColumns.map((c) => <th key={c.name}>{c.label}</th>)}
								<th>Ações</th>
							</tr>
						</thead>
						<tbody>
							{items.length === 0 ? (
								<tr>
									<td colSpan={tableColumns.length + 1}>Nenhum registro encontrado.</td>
								</tr>
							) : (
								items.map((item) => {
									const itemKey = getItemKey(item);
									const isConfirming = confirmDeleteKey === itemKey;
									return (
										<tr key={itemKey}>
											{tableColumns.map((c) => (
												<td key={c.name}>{resolveCellValue(c, item[c.name])}</td>
											))}
											<td style={{ whiteSpace: "nowrap" }}>
												{resource.supportsUpdate && (
													<button
														type="button"
														className="action-btn edit"
														onClick={() => openEdit(item)}
														title="Editar"
													>
														<FontAwesomeIcon icon={faPencil} />
													</button>
												)}
												{isConfirming ? (
													<>
														<button
															type="button"
															className="action-btn confirm"
															onClick={() => handleDelete(item)}
															title="Confirmar exclusão"
														>
															<FontAwesomeIcon icon={faCheck} />
														</button>
														<button
															type="button"
															className="action-btn"
															style={{ background: "#f1f5f9", color: "#64748b" }}
															onClick={() => setConfirmDeleteKey(null)}
															title="Cancelar"
														>
															<FontAwesomeIcon icon={faXmark} />
														</button>
													</>
												) : (
													<button
														type="button"
														className="action-btn delete"
														onClick={() => setConfirmDeleteKey(itemKey)}
														title="Excluir"
													>
														<FontAwesomeIcon icon={faTrash} />
													</button>
												)}
											</td>
										</tr>
									);
								})
							)}
						</tbody>
					</table>
				</div>
			)}

			{modalOpen && (
				<div
					className="modal-overlay"
					role="dialog"
					aria-modal="true"
					onClick={(e) => { if (e.target === e.currentTarget) setModalOpen(false); }}
				>
					<div className="modal-card admin-modal">
						<button
							className="modal-close"
							type="button"
							onClick={() => setModalOpen(false)}
							aria-label="Fechar"
						>
							<FontAwesomeIcon icon={faXmark} />
						</button>
						<h3 style={{ marginBottom: 16 }}>
							{editItem ? "Editar " : "Adicionar "}{resource.label}
						</h3>
						{error && <p className="form-error" style={{ marginBottom: 12 }}>{String(error)}</p>}
						<form onSubmit={handleSubmit} className="form-grid">
							{formFields.map((field) => (
								<label key={field.name}>
									{field.label}
									{renderInput(field)}
								</label>
							))}
							<div className="button-row" style={{ marginTop: 4 }}>
								<button type="submit" disabled={saving}>
									<FontAwesomeIcon icon={faCheck} />
									<span style={{ marginLeft: 7 }}>
										{saving ? "Salvando..." : (editItem ? "Salvar alterações" : "Criar registro")}
									</span>
								</button>
								<button type="button" className="neutral" onClick={() => setModalOpen(false)}>
									Cancelar
								</button>
							</div>
						</form>
					</div>
				</div>
			)}
		</section>
	);
}

// ── Dashboard admin principal ──────────────────────────────────────────────────
function AdminDashboard() {
	const navigate = useNavigate();
	const { user, logout } = useContext(AuthContext);
	const [activeKey, setActiveKey] = useState("livros");

	const activeResource = useMemo(() => getResourceByKey(activeKey), [activeKey]);

	function handleLogout() {
		logout();
		navigate("/");
	}

	return (
		<Layout
			title="Painel Administrativo"
			subtitle={"Bem-vindo, " + (user?.name || "funcionário") + "!"}
			userName={user?.name}
			onLogout={handleLogout}
		>
			<div className="dashboard-grid">
				<aside className="sidebar-nav admin-sidebar">
					<div className="sidebar-brand">
						<FontAwesomeIcon icon={faUserTie} className="sidebar-brand-icon" />
						<span>Administração</span>
					</div>

					<nav style={{ overflowY: "auto" }}>
						{SIDEBAR_GROUPS.map((group) => (
							<div key={group.label}>
								<p className="sidebar-group-label">{group.label}</p>
								{group.keys.map((key) => {
									const res = getResourceByKey(key);
									if (!res) return null;
									return (
										<button
											key={key}
											type="button"
											className={"sidebar-nav-item" + (activeKey === key ? " active" : "")}
											onClick={() => setActiveKey(key)}
										>
											<FontAwesomeIcon icon={RESOURCE_ICONS[key] || faBook} />
											<span>{res.label}</span>
										</button>
									);
								})}
							</div>
						))}
					</nav>

					<div className="sidebar-footer">
						<p className="muted">{user?.email}</p>
					</div>
				</aside>

				<main className="main-content">
					{activeResource && (
						<ResourcePanel key={activeKey} resource={activeResource} />
					)}
				</main>
			</div>
		</Layout>
	);
}

export default AdminDashboard;
