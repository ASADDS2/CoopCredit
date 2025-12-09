#!/bin/bash

echo "🛑 Stopping CoopCredit Microservices..."
docker-compose down

echo "✅ All services stopped successfully!"
echo ""
echo "💡 To remove volumes (database data), run:"
echo "   docker-compose down -v"
echo ""

