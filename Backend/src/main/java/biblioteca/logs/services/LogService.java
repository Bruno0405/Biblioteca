package biblioteca.logs.services;

import biblioteca.logs.data.Log;
import biblioteca.logs.repository.RepositorioLogs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class LogService {

    @Inject
    RepositorioLogs repositorioLogs;

    @Transactional
    public void log(Log log) {
        if (log.getDataAcao() == null) {
            log.setDataAcao(LocalDateTime.now());
        }
        repositorioLogs.persist(log);
    }
}
