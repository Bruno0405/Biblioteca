import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
	faArrowsRotate,
	faBook,
	faBookmark,
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
	"historico": faClockRotateLeft,
	livros: faBook,
	logs: faFileLines,
	"movimentacao": faArrowsRotate,
	multas: faMoneyCheckDollar,
	reservas: faBookmark,
};

const SIDEBAR_GROUPS = [
	{ label: "Acervo", keys: ["livros", "autores", "generos", "fotos"] },
	{ label: "Pessoas", keys: ["clientes", "funcionarios"] },
	{ label: "Operações", keys: ["estoque", "movimentacao", "reservas", "multas"] },
	{ label: "Sistema", keys: ["logs", "historico"] },
];

// ── Dropdown multiselect com checkboxes ──────────────────────────────────────
function MultiSelectDropdown({ options, selected, onChange, idField, labelField, placeholder }) {
	const [open, setOpen] = useState(false);
	const ref = useRef(null);

	useEffect(() => {
		function handleClickOutside(e) {
			if (ref.current && !ref.current.contains(e.target)) setOpen(false);
		}
		document.addEventListener("mousedown", handleClickOutside);
		return () => document.removeEventListener("mousedown", handleClickOutside);
	}, []);

	function add(id) {
		const numId = Number(id);
		if (!selected.includes(numId)) onChange([...selected, numId]);
	}

	function remove(id) {
		onChange(selected.filter((i) => i !== Number(id)));
	}

	const available = options.filter((o) => !selected.includes(Number(o[idField])));
	const selectedItems = options.filter((o) => selected.includes(Number(o[idField])));

	return (
		<div ref={ref} style={{ display: "flex", flexDirection: "column", gap: 6 }}>
			{/* Trigger */}
			<div style={{ position: "relative" }}>
				<button
					type="button"
					onClick={() => setOpen((v) => !v)}
					style={{
						width: "100%", textAlign: "left", padding: "7px 32px 7px 10px",
						background: "var(--bg-2, #f8fafc)", border: "1px solid var(--border, #e2e8f0)",
						borderRadius: 6, cursor: "pointer", fontSize: 14,
						color: "var(--text-soft, #94a3b8)",
						boxShadow: "none", fontFamily: "inherit",
					}}
				>
					{placeholder || "Adicionar..."}
					<span style={{ position: "absolute", right: 10, top: "50%", transform: open ? "translateY(-50%) rotate(180deg)" : "translateY(-50%)", pointerEvents: "none", fontSize: 11, color: "var(--text-soft, #94a3b8)", transition: "transform .15s" }}>▼</span>
				</button>
				{open && (
					<ul style={{
						position: "absolute", top: "calc(100% + 4px)", left: 0, right: 0,
						background: "var(--bg, #fff)", border: "1px solid var(--border, #e2e8f0)",
						borderRadius: 6, boxShadow: "0 4px 16px rgba(0,0,0,.10)",
						margin: 0, padding: "4px 0", listStyle: "none",
						maxHeight: 200, overflowY: "auto", zIndex: 9999,
					}}>
						{available.length === 0 && (
							<li style={{ padding: "8px 12px", color: "var(--text-soft, #94a3b8)", fontSize: 13 }}>Todos já selecionados</li>
						)}
						{available.map((opt) => {
							const id = opt[idField];
							return (
								<li
									key={id}
									onMouseDown={(e) => { e.preventDefault(); e.stopPropagation(); }}
									onClick={() => add(id)}
									style={{
										padding: "7px 12px", cursor: "pointer", fontSize: 14,
										color: "var(--text, #1e293b)", userSelect: "none",
									}}
									onMouseEnter={(e) => e.currentTarget.style.background = "var(--accent-soft, #eff6ff)"}
									onMouseLeave={(e) => e.currentTarget.style.background = "transparent"}
								>
									{opt[labelField] || id}
								</li>
							);
						})}
					</ul>
				)}
			</div>
			{/* Selected tags */}
			{selectedItems.length > 0 && (
				<div style={{ display: "flex", flexWrap: "wrap", gap: 6 }}>
					{selectedItems.map((opt) => {
						const id = opt[idField];
						return (
							<span key={id} style={{
								display: "inline-flex", alignItems: "center", gap: 5,
								padding: "3px 8px 3px 10px",
								background: "var(--accent-soft, #eff6ff)",
								border: "1px solid var(--accent, #3b82f6)",
								color: "var(--accent, #3b82f6)",
								borderRadius: 999, fontSize: 13, fontWeight: 500,
							}}>
								{opt[labelField] || id}
								<button
									type="button"
									onClick={() => remove(id)}
									style={{
										background: "none", border: "none", cursor: "pointer",
										padding: "0 2px", lineHeight: 1, fontSize: 14,
										color: "var(--accent, #3b82f6)", display: "flex", alignItems: "center",
									}}
									title="Remover"
								>×</button>
							</span>
						);
					})}
				</div>
			)}
		</div>
	);
}

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
	const [deleteToast, setDeleteToast] = useState(null);

	useEffect(() => {
		if (!deleteToast) return;
		const t = setTimeout(() => setDeleteToast(null), 3500);
		return () => clearTimeout(t);
	}, [deleteToast]);

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
		}).catch(() => { });
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
			if (f.lookup?.multi && f.lookup.sourceField) {
				const sourceData = item[f.lookup.sourceField];
				const lookupRes = getResourceByKey(f.lookup.resource);
				const idField = f.lookup.idField || (lookupRes ? lookupRes.idFields[0] : "id");
				data[f.name] = Array.isArray(sourceData) ? sourceData.map((s) => Number(s[idField])) : [];
			} else {
				data[f.name] = item[f.name] ?? "";
			}
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
			const errMsg = e.response?.data;
			setError(typeof errMsg === "string" ? errMsg : JSON.stringify(errMsg) || "Erro ao salvar. Verifique os dados.");
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
			setDeleteToast({ ok: true, msg: "Registro excluído com sucesso." });
			await carregarDados();
		} catch (e) {
			setConfirmDeleteKey(null);
			const errMsg = e.response?.data;
			let msg;
			if (typeof errMsg === "string" && !errMsg.trim().startsWith("<")) {
				msg = errMsg;
			} else if (errMsg?.message) {
				msg = errMsg.message;
			} else if (e.response?.status === 409 || e.response?.status === 500) {
				msg = "Não foi possível excluir: este registro possui dados vinculados.";
			} else {
				msg = "Erro ao excluir.";
			}
			setDeleteToast({ ok: false, msg });
		}
	}

	const tableColumns = useMemo(() => {
		const seen = new Set(resource.idFields);
		const idCols = resource.idFields.map((f) => ({ name: f, label: "ID" }));
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

		// Multi-select lookup (relação N:N)
		if (field.lookup?.multi) {
			const lookupRes = getResourceByKey(field.lookup.resource);
			const idField = field.lookup.idField || (lookupRes ? lookupRes.idFields[0] : "id");
			const options = lookupData[field.lookup.resource] || [];
			const selected = Array.isArray(value) ? value.map(Number) : [];
			return (
				<MultiSelectDropdown
					options={options}
					selected={selected}
					onChange={(v) => setField(field.name, v)}
					idField={idField}
					labelField={field.lookup.labelField}
					placeholder="Selecione..."
				/>
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

	function formatCPF(val) {
		if (!val) return val;
		const d = String(val).replace(/\D/g, "");
		if (d.length !== 11) return val;
		return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9, 11)}`;
	}

	function formatPhone(val) {
		if (!val) return val;
		const d = String(val).replace(/\D/g, "");
		if (d.length === 11) return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7, 11)}`;
		if (d.length === 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6, 10)}`;
		return val;
	}

	function formatISBN(val) {
		if (!val) return val;
		const d = String(val).replace(/\D/g, "");
		if (d.length !== 13) return val;
		return `${d.slice(0, 3)}-${d.slice(3, 4)}-${d.slice(4, 6)}-${d.slice(6, 12)}-${d.slice(12, 13)}`;
	}

	function resolveCellValue(col, rawValue, item = null) {
		// ID columns: add # prefix
		if (resource.idFields.includes(col.name)) {
			return rawValue != null && rawValue !== "" ? `#${rawValue}` : "-";
		}

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

		// Resolve multi-lookup (N:N)
		if (field.lookup?.multi) {
			const sourceField = field.lookup.sourceField;
			const source = item && sourceField ? item[sourceField] : null;
			if (Array.isArray(source) && source.length > 0) {
				const names = source.map((s) => s[field.lookup.labelField] || s[field.lookup.idField]).join(", ");
				return truncate(names);
			}
			return "-";
		}

		// Resolve nome do lookup (FK)
		if (field.lookup) {
			const lookupRes = getResourceByKey(field.lookup.resource);
			const idField = field.lookup.idField || (lookupRes ? lookupRes.idFields[0] : "id");
			const options = lookupData[field.lookup.resource] || [];
			const found = options.find((o) => String(o[idField]) === String(rawValue));
			return found ? (found[field.lookup.labelField] || truncate(rawValue)) : truncate(rawValue);
		}

		// Formatação de campos conhecidos
		if (col.name === "cpf" && rawValue) return formatCPF(rawValue);
		if (col.name === "telefone" && rawValue) return formatPhone(rawValue);
		if (col.name === "isnb" && rawValue) return formatISBN(rawValue);

		// Boolean: ícones
		if (field.type === "boolean") {
			if (rawValue === true || rawValue === "true") {
				return <FontAwesomeIcon icon={faCheck} style={{ color: "var(--success)" }} />;
			}
			if (rawValue === false || rawValue === "false") {
				return <FontAwesomeIcon icon={faXmark} style={{ color: "var(--danger)" }} />;
			}
			return "-";
		}

		return truncate(rawValue);
	}

	function truncate(value) {
		const str = String(value ?? "-");
		return str.length > 32 ? str.slice(0, 32) + "…" : str;
	}

	return (
		<section className="panel admin-resource-panel" style={{ position: "relative" }}>
			{deleteToast && (
				<div style={{
					position: "fixed", bottom: 24, right: 24, zIndex: 9999,
					background: deleteToast.ok ? "var(--success, #16a34a)" : "var(--danger, #dc2626)",
					color: "#fff", padding: "12px 20px", borderRadius: 8,
					boxShadow: "0 4px 16px rgba(0,0,0,0.18)", fontSize: 14,
					maxWidth: 360, lineHeight: 1.4,
				}}>
					{deleteToast.msg}
				</div>
			)}
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
												<td key={c.name}>{resolveCellValue(c, item[c.name], item)}</td>
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

					<nav>
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
