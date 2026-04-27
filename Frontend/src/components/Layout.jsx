function Layout({ title, subtitle, userName, onLogout, children }) {
	return (
		<div className="app-shell">
			<header className="app-header">
				<div>
					<p className="kicker">Biblioteca</p>
					<h1>{title}</h1>
					{subtitle && <p className="muted">{subtitle}</p>}
				</div>

				<div className="header-actions">
					<span className="chip">{userName || "Usuario"}</span>
					<button type="button" className="danger-outline" onClick={onLogout}>
						Sair
					</button>
				</div>
			</header>

			<main>{children}</main>
		</div>
	);
}

export default Layout;
