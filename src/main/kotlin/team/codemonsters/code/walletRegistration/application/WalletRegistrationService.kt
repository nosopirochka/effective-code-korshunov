package team.codemonsters.code.walletRegistration.application

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import team.codemonsters.code.walletRegistration.domain.Client
import team.codemonsters.code.walletRegistration.domain.ClientId
import team.codemonsters.code.walletRegistration.domain.WalletRegistrationRequest
import team.codemonsters.code.walletRegistration.presentation.WalletRegistrationDTO

@Service
class WalletRegistrationService(
    private val clientGateway: ClientGateway,
    private val walletGateway: WalletGateway
) {
    private val log = LoggerFactory.getLogger(WalletRegistrationService::class.java)
    fun registerWallet(clientId: ClientId): Result<Client> {
        val clientResult = clientGateway.findClient(clientId)
        if (clientResult.isFailure)
            return Result.failure(clientResult.exceptionOrNull()!!)

        val walletId = walletGateway.registerWallet()
        if (walletId.isFailure)
            return Result.failure(walletId.exceptionOrNull()!!)

        val walletRegistrationRequest = WalletRegistrationRequest.emerge(
            clientResult.getOrThrow(),
            walletId.getOrThrow()
        )
        if (walletRegistrationRequest.isFailure)
            return Result.failure(walletRegistrationRequest.exceptionOrNull()!!)

        return clientGateway.registerWallet(walletRegistrationRequest.getOrThrow())
    }

}