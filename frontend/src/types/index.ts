export interface Usuario {
    token : string,
    tipoUsuario : string
}

export interface Agendamento {
    id : number,
    nomeCliente :string,
    nomeBarbeiro : string,
    dataHora : string,
    status : string
}

export interface HorarioDisponivel{
    dataHora: string
}