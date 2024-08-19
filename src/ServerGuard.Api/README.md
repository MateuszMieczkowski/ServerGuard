Add migration

Go to root folder of the solution and run the following command:
dotnet ef migrations add "Migration" --project ./src/ServerGuard.Infrastructure/ServerGuard.Infrastructure.csproj --startup-project ./src/ServerGuard.Api/ServerGuard.Api.csproj -o Data/Migrations

Update database

Go to root folder of the solution and run the following command:
dotnet ef database update --project ./src/ServerGuard.Infrastructure/ServerGuard.Infrastructure.csproj --startup-project ./src/
ServerGuard.Api/ServerGuard.Api.csproj
